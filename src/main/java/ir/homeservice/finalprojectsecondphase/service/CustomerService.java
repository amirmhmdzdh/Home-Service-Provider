package ir.homeservice.finalprojectsecondphase.service;

import cn.apiclub.captcha.Captcha;
import ir.homeservice.finalprojectsecondphase.dto.CustomerIdOrderId;
import ir.homeservice.finalprojectsecondphase.dto.PaymentPageDTO;
import ir.homeservice.finalprojectsecondphase.dto.request.*;
import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.mapper.CustomerMappers;
import ir.homeservice.finalprojectsecondphase.model.address.Address;
import ir.homeservice.finalprojectsecondphase.model.comment.Comment;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.Users;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.repository.CustomerRepository;
import ir.homeservice.finalprojectsecondphase.utill.CaptchaUtil;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final Validation validation;
    private final OrderService orderService;
    private final OfferService offerService;
    protected final EmailService emailService;
    @PersistenceContext
    private final EntityManager entityManager;
    private final AddressService addressService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;
    private final SubServiceService subServiceService;
    private final SpecialistService specialistService;
    private final CustomerRepository customerRepository;


    public Customer signUpCustomer(CustomerRequest customer) {
        Address address = Address.builder().province(customer.request().province()).city(customer.request().city())
                .avenue(customer.request().avenue()).houseNumber(customer.request().houseNumber()).build();
        Customer insertCustomer = Customer.builder()
                .firstName(customer.firstName())
                .lastName(customer.lastName())
                .email(customer.email())
                .password(passwordEncoder.encode(customer.password()))
                .registrationTime(LocalDateTime.now())
                .credit(0L)
                .role(Role.CUSTOMER)
                .address(address)
                .build();
        if (customerRepository.findByEmail(customer.email()).isPresent())
            throw new DuplicateInformationException(customer.email() + " is duplicate");
        customerRepository.save(insertCustomer);
        emailService.createEmail(customer.email());
        addAddress(address, insertCustomer);
        return insertCustomer;
    }

    public Customer signInCustomer(Customer requestSignIn) {
        return customerRepository.findByEmailAndPassword(requestSignIn.getEmail(), requestSignIn.getPassword())
                .orElseThrow(() -> new NotFoundException("This customer does not exist!"));
    }

    public Customer changePassword(UserChangePasswordRequest password, Long customerId) {
        if (!password.newPassword().equals(password.confirmNewPassword()))
            throw new NotFoundException("this confirmNewPassword not match with newPassword!");
        Customer customer = customerRepository.getReferenceById(customerId);
        customer.setPassword(passwordEncoder.encode(password.confirmNewPassword()));
        return customerRepository.save(customer);
    }

    public Orders watchAndOrder(Long customerId, OrdersRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        SubService subService = subServiceService.findById(request.subServiceId())
                .orElseThrow(() ->
                        new NotFoundException("This subService does not exist!"));
        validation.validatePrice(subService, request);
        Orders orders = Orders.builder()
                .description(request.description()).subServices(subService).orderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION)
                .executionTime(request.workStartDate()).endTime(request.workEndDate())
                .customer(customer).address(customer.getAddress()).proposedPrice(request.proposedPrice())
                .registrationTime(LocalDateTime.now()).build();
        validation.validateTime(orders);
        return orderService.save(orders);
    }

    public void addAddress(Address address, Customer customer) {
        Customer customer1 = customerRepository.getReferenceById(customer.getId());
        address.setCustomer(customer1);
        customer1.setAddress(address);
        addressService.createAddress(address);
    }

    public Orders trackOrders(Long offerId, Customer customer) {
        validation.checkOfferBelongToTheOrder(offerId, customer);
        Optional<Offer> offer = offerService.findById(offerId);
        Offer foundOffer = offer.get();
        if (foundOffer.getOfferStatus().equals(OfferStatus.ACCEPTED))
            throw new DuplicateInformationException(" this offer already accepted");
        return orderService.chooseOffer(foundOffer.getOrders(), offerId);
    }

    public List<Offer> findOfferListByProposedPrice(Long orderId, Customer customer) {
        validation.checkOwnerOfTheOrder(orderId, customer);
        return offerService.findOfferListByProposedPrice(orderId);
    }

    public List<Offer> findOfferListBySpecialistScore(Long orderId, Customer customer) {
        validation.checkOwnerOfTheOrder(orderId, customer);
        return offerService.findOfferListBySpecialistScore(orderId);
    }

    public Orders changeOrderStatusToStarted(Long orderId, Users customer) {
        validation.checkOwnerOfTheOrder(orderId, (Customer) customer);
        Optional<Orders> optionalOrders = orderService.findById(orderId);
        if (!optionalOrders.get().getOrderStatus().equals(OrderStatus.WAITING_FOR_SPECIALIST_TO_COME))
            throw new NotFoundException
                    ("the status of this order is not yet WAITING FOR EXPERT TO COME!");
        optionalOrders.get().getOfferList().forEach(offer -> {
            if (offer.getOfferStatus().equals(OfferStatus.ACCEPTED))
                if (offer.getExecutionTime().isBefore(LocalDateTime.now()))
                    throw new TimeException("the specialist has not arrived at your place yet!");
        });
        optionalOrders.get().setOrderStatus(OrderStatus.STARTED);
        return orderService.save(optionalOrders.get());
    }

    public Orders changeOrderStatusToDone(Long orderId, Customer customer) {
        // Customer customer = customerRepository.getReferenceById(customerId);
        validation.checkOwnerOfTheOrder(orderId, customer);
        Orders order = orderService.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getOrderStatus().equals(OrderStatus.STARTED))
            throw new NotFoundException("the status of this order is not yet STARTED!");

        Optional<Offer> acceptedOffer = order.getOfferList().stream()
                .filter(o -> o.getOfferStatus().equals(OfferStatus.ACCEPTED))
                .findFirst();

        if (acceptedOffer.isEmpty())
            throw new NotFoundException("Not Exist accept Offer");

        Offer offer = acceptedOffer.get();
        order.setOrderStatus(OrderStatus.DONE);
        orderService.save(order);

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime = offer.getEndTime();

        if (currentTime.isAfter(endTime)) {
            long minuteDelay = ChronoUnit.MINUTES.between(endTime, currentTime);
            double hourDelay = (double) minuteDelay / 60;

            Specialist specialist = offer.getSpecialist();
            specialist.delay(hourDelay);
            specialistService.save(specialist);
        }
        return order;
    }

    public Customer increaseCustomerCredit(Customer customer, Long credit) {
        customer.setCredit(credit + customer.getCredit());
        return customerRepository.save(customer);
    }

    public Customer paidByInAppCredit(Long orderId, Customer customer) {
        validation.checkOwnerOfTheOrder(orderId, customer);
        Orders orders = orderService.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order not Exist!"));
        Long customerCredit = customer.getCredit();
        Offer offer = orders.getOfferList().stream()
                .filter(o -> o.getOfferStatus().equals(OfferStatus.ACCEPTED)).findFirst().get();
        Long offerProposedPrice = offer.getProposedPrice();

        if (customerCredit < offerProposedPrice) {
            throw new PriceException("not enough credit to pay in app");
        }
        orders.setOrderStatus(OrderStatus.PAID);
        orderService.save(orders);
        accounting(orders);
        customer.setCredit(customerCredit - offerProposedPrice);
        return customerRepository.save(customer);
    }

    private void accounting(Orders orders) {
        orders.setOrderStatus(OrderStatus.PAID);
        Offer offer = orders.getOfferList().stream().filter(o ->
                o.getOfferStatus().equals(OfferStatus.ACCEPTED)).findFirst().get();
        orderService.save(orders);
        Long proposedPrice = offer.getProposedPrice();
        Specialist specialist = offer.getSpecialist();
        long specialistShare = Math.round(proposedPrice * 0.7);
        specialist.setCredit(specialist.getCredit() + specialistShare);
        specialistService.save(specialist);
    }

    public ModelAndView payByOnlinePayment(Long orderId, Customer customer, Model model) {
        Orders orders = orderService.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order not Exist!"));
        validation.checkOwnerOfTheOrder(orders.getId(), customer);
        PaymentPageDTO paymentPageDTO = new PaymentPageDTO();
        CustomerIdOrderId customerIdOrderId = new CustomerIdOrderId(orders.getId(), customer.getId());
        paymentPageDTO.setCustomerIdOrderId(customerIdOrderId);
        paymentPageDTO.setPrice(paymentPriceCalculator(orders.getId()));
        setupCaptcha(paymentPageDTO);
        model.addAttribute("dto", paymentPageDTO);
        return new ModelAndView("payment");
    }

    private Long paymentPriceCalculator(Long orderId) {
        Optional<Orders> order = orderService.findById(orderId);
        OrderStatus orderStatus = order.get().getOrderStatus();
        if (!orderStatus.equals(OrderStatus.DONE)) {
            if (orderStatus.equals(OrderStatus.PAID))
                throw new NotFoundException(
                        "the cost of this order has already been PAID!");
            else
                throw new NotFoundException(
                        "This order has not yet reached the payment stage, this order is in the " +
                                orderStatus + " stage!");
        }
        Offer offer = order.get().getOfferList().stream().
                filter(o -> o.getOfferStatus().equals(OfferStatus.ACCEPTED)).findFirst().get();

        return offer.getProposedPrice();
    }


    private void setupCaptcha(PaymentPageDTO dto) {
        Captcha captcha = CaptchaUtil.createCaptcha(350, 100);
        dto.setHidden(captcha.getAnswer());
        dto.setCaptcha("");
        dto.setImage(CaptchaUtil.encodeBase64(captcha));
    }

    public Customer changeOrderStatusToPaidByOnlinePayment(CustomerIdOrderId dto) {
        Optional<Customer> customer = customerRepository.findById(dto.getCustomerId());
        if (customer.isEmpty())
            throw new NotFoundException("not found user");
        Optional<Orders> order = orderService.findById(dto.getOrderId());
        if (order.isEmpty())
            throw new NotFoundException("not found order");

        return paidByInAppCredit(order.get().getId(), customer.get());
    }

    public Comment registerComment(CommentRequest request, Long orderId, Customer customer) {
        validation.checkOwnerOfTheOrder(orderId, customer);
        Comment comment;
        Optional<Orders> orders = orderService.findById(orderId);
        if (!orders.get().getOrderStatus().equals(OrderStatus.PAID)) {
            throw new NotFoundException("The status of this order is not yet PAID!");
        }
        Optional<Offer> acceptedOffer = orders.get().getOfferList().stream()
                .filter(o -> o.getOfferStatus().equals(OfferStatus.ACCEPTED))
                .findFirst();

        if (acceptedOffer.isPresent()) {
            Offer offer = acceptedOffer.get();
            Specialist specialist = offer.getSpecialist();

            double existingStars = specialist.getStar();
            double newStars = request.star();
            double totalStars = existingStars + newStars;

            specialist.setStar(totalStars);
            specialistService.save(specialist);
        }
        comment = Comment.builder()
                .orders(orders.get())
                .textComment(request.textComment())
                .star(request.star())
                .registrationTime(LocalDateTime.now())
                .build();
        commentService.save(comment);
        orders.get().setComment(comment);
        orderService.save(orders.get());
        return comment;
    }

    public List<FilterUserResponse> customerFilter(SearchForUser customerSearch) {
        List<FilterUserResponse> filterUserResponse = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> customerCriteriaQuery = criteriaBuilder.createQuery(Customer.class);
        Root<Customer> customerRoot = customerCriteriaQuery.from(Customer.class);
        List<Predicate> predicateList = new ArrayList<>();

        Optional.ofNullable(customerSearch.getFirstName())
                .map(firstname -> criteriaBuilder.equal(customerRoot.get("firstName"), firstname))
                .ifPresent(predicateList::add);

        Optional.ofNullable(customerSearch.getLastName())
                .map(lastname -> criteriaBuilder.equal(customerRoot.get("lastName"), lastname))
                .ifPresent(predicateList::add);

        Optional.ofNullable(customerSearch.getEmail())
                .map(email -> criteriaBuilder.equal(customerRoot.get("email"), email))
                .ifPresent(predicateList::add);

        Optional.ofNullable(customerSearch.getUserType())
                .map(user -> criteriaBuilder.equal(customerRoot.get("role"), user))
                .ifPresent(predicateList::add);

        if (customerSearch.getMinUserCreationAt() == null && customerSearch.getMaxUserCreationAt() != null) {
            customerSearch.setMinUserCreationAt(LocalDateTime.now().minusYears(1));
        }
        if (customerSearch.getMinUserCreationAt() != null && customerSearch.getMaxUserCreationAt() == null) {
            customerSearch.setMaxUserCreationAt(LocalDateTime.now());
        }
        if (customerSearch.getMinUserCreationAt() != null && customerSearch.getMaxUserCreationAt() != null) {
            predicateList.add(criteriaBuilder.between(customerRoot.get("registrationTime"),
                    customerSearch.getMinUserCreationAt(), customerSearch.getMaxUserCreationAt()));
        }

        customerCriteriaQuery.select(customerRoot).where(criteriaBuilder.or(predicateList.toArray(new Predicate[0])));
        List<Customer> resultList = entityManager.createQuery(customerCriteriaQuery).getResultList();
        resultList.forEach(customer -> filterUserResponse.add(CustomerMappers.convertToFilterDTO(customer)));
        return filterUserResponse;
    }

    public List<Orders> filterOrder(String orderStatus, Long clientId) {
        Optional<Customer> client = customerRepository.findById(clientId);
        List<Orders> dbOrderList = client.get().getOrdersList();
        if (dbOrderList.isEmpty()) {
            throw new NotFoundException("No orders found for the client");
        }
        List<Orders> orderList = dbOrderList.stream()
                .filter(o -> o.getOrderStatus().name().equals(orderStatus))
                .collect(Collectors.toList());
        if (orderList.isEmpty()) {
            throw new NotFoundException("No orders found with the given order status");
        }
        return orderList;
    }

    public Long getCustomerCredit(Long clientId) {
        Optional<Customer> client = customerRepository.findById(clientId);
        return client.get().getCredit();
    }

    public Optional<Customer> findByEmail(String email) {
        return Optional.ofNullable(customerRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(
                        String.format("USER %s NOT FOUND", email)
                )
        ));
    }
}






