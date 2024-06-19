package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.address.Address;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.repository.CustomerRepository;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressService addressService;
    private final OrderService orderService;
    private final SubServiceService subServiceService;
    private final Validation validation;
    private final OfferService offerService;

    public void signUpCustomer(Customer customer) {
        Customer insertCustomer = Customer.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .password(customer.getPassword())
                .registrationTime(customer.getRegistrationTime())
                .credit(customer.getCredit())
                .role(Role.CUSTOMER)
                .build();
        validation.checkText(customer.getFirstName());
        validation.checkText(customer.getLastName());
        validation.checkEmail(customer.getEmail());
        validation.checkPassword(customer.getPassword());
        validation.checkNumber(customer.getCredit());
        if (customerRepository.findByEmail(customer.getEmail()).isPresent())
            throw new DuplicateEmailException(customer.getEmail() + " is duplicate");
        customerRepository.save(insertCustomer);
    }

    public Customer signInCustomer(String email, String password) {
        validation.checkEmail(email);
        validation.checkPassword(password);
        return customerRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomerStatusException("This customer does not exist!"));
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        validation.checkEmail(email);
        validation.checkPassword(oldPassword);
        validation.checkPassword(newPassword);
        Optional<Customer> customer = customerRepository.findAll().stream()
                .filter(c -> email.equals(c.getEmail()) && oldPassword.equals(c.getPassword()))
                .findFirst();
        if (customer.isEmpty()) {
            throw new DuplicateEmailException("Invalid email or old password.");
        }
        Customer customer1 = customer.get();
        customer1.setPassword(newPassword);
        customerRepository.save(customer1);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public void watchAndOrder(Orders orders, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerStatusException("Customer not found"));
        validation.validateTime(orders);
        validation.checkBlank(orders.getSubServices().getName());
        validation.checkNumber(orders.getProposedPrice());
        validation.checkBlank(orders.getDescription());
        SubService subService = subServiceService.findById(orders.getSubServices().getId())
                .orElseThrow(() ->
                        new SubServicesIsNotExistException("This subService does not exist!"));
        validation.validatePrice(subService, orders);
        orders.setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION);
        orders.setCustomer(customer);
        orderService.save(orders);
    }

    public void addAddress(Address address, Customer customer) {
        validation.checkBlank(String.valueOf(address));
        Optional<Customer> customerOptional = customerRepository.findById(customer.getId());
        address.setCustomer(customerOptional.get());
        customerOptional.get().addAddress(address);
        addressService.createAddress(address);
    }

    public List<Offer> findOfferListByProposedPrice(Long orderId, Customer customer) {
        validation.checkNumber(orderId);
        validation.checkOwnerOfTheOrder(orderId, customer);
        return offerService.findOfferListByProposedPrice(orderId);
    }

    public List<Offer> findOfferListBySpecialistScore(Long orderId, Customer customer) {
        validation.checkNumber(orderId);
        validation.checkOwnerOfTheOrder(orderId, customer);
        return offerService.findOfferListBySpecialistScore(orderId);
    }

    public void trackOrders(Long offerId, Customer customer) {
        validation.checkNumber(offerId);
        validation.checkOfferBelongToTheOrder(offerId, customer);
        Optional<Offer> offer = offerService.findById(offerId);
        Offer foundOffer = offer.get();
        if (foundOffer.getOfferStatus().equals(OfferStatus.ACCEPTED))
            throw new OfferStatusException(" this offer already accepted");
        orderService.chooseOffer(foundOffer.getOrders(), offerId);
    }

    public void notificationOfStatus(Long orderId, Customer customer) {
        validation.checkNumber(orderId);
        validation.checkOwnerOfTheOrder(orderId, customer);
        Optional<Orders> optionalOrders = orderService.findById(orderId);
        if (!optionalOrders.get().getOrderStatus().equals(OrderStatus.WAITING_FOR_SPECIALIST_TO_COME))
            throw new OrderIsNotExistException
                    ("the status of this order is not yet \"WAITING FOR EXPERT TO COME\"!");
        optionalOrders.get().getOfferList().forEach(offer -> {
            if (offer.getOfferStatus().equals(OfferStatus.ACCEPTED))
                if (offer.getExecutionTime().isBefore(LocalDateTime.now()))
                    throw new TimeException("the specialist has not arrived at your place yet!");
        });
        optionalOrders.get().setOrderStatus(OrderStatus.STARTED);
        optionalOrders.get().setOrderStatus(OrderStatus.DONE);
        orderService.save(optionalOrders.get());
    }
}
