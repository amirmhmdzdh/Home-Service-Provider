package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.OfferRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.SearchForUser;
import ir.homeservice.finalprojectsecondphase.dto.request.UserChangePasswordRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.mapper.SpecialistMappers;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import ir.homeservice.finalprojectsecondphase.repository.SpecialistRepository;
import ir.homeservice.finalprojectsecondphase.utill.SaveImageToFile;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class SpecialistService {
    @PersistenceContext
    private EntityManager entityManager;
    private final Validation validation;
    private final EmailService emailService;
    private final OfferService offerService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;
    private final SpecialistRepository specialistRepository;

    public Specialist signUpSpecialist(Specialist specialist, String imagePath) {
        if (specialistRepository.findByEmail(specialist.getEmail()).isPresent())
            throw new DuplicateInformationException("this Email already exist!");
        byte[] image = validation.checkImage(imagePath);
        Specialist specialist1 = Specialist.builder()
                .firstName(specialist.getFirstName()).lastName(specialist.getLastName()).email(specialist.getEmail())
                .status(SpecialistStatus.NEW).registrationTime(LocalDateTime.now()).image(image).credit(0L)
                .password(passwordEncoder.encode(specialist.getPassword())).role(Role.SPECIALIST).star(0d).doneOrders(0)
                .build();
        specialistRepository.save(specialist1);
        SaveImageToFile.saveImageToFile(image, "D:\\test.jpg");
        emailService.createEmail(specialist.getEmail());
        specialist1.setStatus(SpecialistStatus.AWAITING);
        return specialistRepository.save(specialist1);
    }

    public Specialist signInSpecialist(String email, String password) {
        return specialistRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new NotFoundException("Specialist not found. "));
    }

    public void changePasswordSpecialist(UserChangePasswordRequest request, Long specialistId) {
        if (!request.newPassword().equals(request.confirmNewPassword()))
            throw new NotFoundException("this confirmNewPassword not match with newPassword!");
        Specialist specialist = specialistRepository.getReferenceById(specialistId);
        specialist.setPassword(passwordEncoder.encode(request.confirmNewPassword()));
        save(specialist);
    }

    public Offer newOffers(OfferRequest offerRequest, Long specialistId) {
        Optional<Specialist> specialistOptional = specialistRepository.findById(specialistId);
        Specialist specialist = specialistOptional.get();
        if (!specialist.getStatus().equals(SpecialistStatus.CONFIRMED))
            throw new NotFoundException("the status of specialist is not CONFIRMED");
        Optional<Orders> ordersOptional = orderService.findById(offerRequest.orderId());
        OrderStatus orderStatus = ordersOptional.get().getOrderStatus();
        if (!(orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION) ||
                orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION)))
            throw new DuplicateInformationException("this order has already accepted the offer");
        if (!(specialist.getSubServicesList().contains(ordersOptional.get().getSubServices())))
            throw new NotFoundException("this specialist does not have such job title!");
        if (ordersOptional.get().getProposedPrice() > offerRequest.offerProposedPrice())
            throw new PriceException("the proposed-price should not be lower than the order proposed-price!");
        if (offerRequest.proposedStartDate().isBefore(ordersOptional.get().getExecutionTime()))
            throw new TimeException("no order has been in your proposed time for begin job!");
        if (offerRequest.proposedEndDate().isBefore(offerRequest.proposedStartDate()))
            throw new TimeException("time does not go back!");
        Offer offer = Offer.builder()
                .specialist(specialist).orders(ordersOptional.get()).offerStatus(OfferStatus.WAITING)
                .sendTime(LocalDateTime.now()).executionTime(offerRequest.proposedStartDate())
                .endTime(offerRequest.proposedEndDate()).proposedPrice(offerRequest.offerProposedPrice()).build();
        offerService.save(offer);
        if (orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION))
            ordersOptional.get().setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION);
        orderService.save(ordersOptional.get());
        return offer;
    }

    public List<FilterUserResponse> specialistFilter(SearchForUser search) {
        List<Predicate> predicateList = new ArrayList<>();
        List<FilterUserResponse> filterUserResponse = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Specialist> specialistCriteriaQuery = criteriaBuilder.createQuery(Specialist.class);
        Root<Specialist> specialistRoot = specialistCriteriaQuery.from(Specialist.class);

        Join<Specialist, SubService> subServiceJoin = specialistRoot.join("subServicesList");

        specialistCriteriaQuery.select(specialistRoot)
                .where(criteriaBuilder.like(subServiceJoin.get("name"), search.getSubServiceName()));


        Optional.ofNullable(search.getFirstName())
                .map(firstName -> criteriaBuilder.equal(specialistRoot.get("firstName"), firstName))
                .ifPresent(predicateList::add);

        Optional.ofNullable(search.getLastName())
                .map(lastName -> criteriaBuilder.equal(specialistRoot.get("lastName"), lastName))
                .ifPresent(predicateList::add);

        Optional.ofNullable(search.getEmail())
                .map(email -> criteriaBuilder.equal(specialistRoot.get("email"), email))
                .ifPresent(predicateList::add);


        Optional.ofNullable(search.getUserStatus())
                .map(userStatus -> criteriaBuilder.equal(specialistRoot.get("userStatus"), userStatus))
                .ifPresent(predicateList::add);


        Optional.ofNullable(search.getMinUserCreationAt())
                .flatMap(minCreationAt -> Optional.ofNullable(search.getMaxUserCreationAt())
                        .map(maxCreationAt -> criteriaBuilder.between(specialistRoot.get("registrationTime"),
                                minCreationAt, maxCreationAt)))
                .ifPresent(predicateList::add);

        Optional.ofNullable(search.getMinScore())
                .flatMap(minScore -> Optional.ofNullable(search.getMaxScore())
                        .map(maxScore -> criteriaBuilder.between(specialistRoot.get("star"), minScore, maxScore)))
                .ifPresent(predicateList::add);


        Optional.ofNullable(search.getMinDoneOrders())
                .flatMap(minDoneOrders -> Optional.ofNullable(search.getMaxDoneOrders())
                        .map(maxDoneOrders -> criteriaBuilder.between(specialistRoot.get("doneOrders"),
                                minDoneOrders, maxDoneOrders)))
                .ifPresent(predicateList::add);

        specialistCriteriaQuery.select(specialistRoot).where(criteriaBuilder.and(predicateList.toArray(new Predicate[0])));
        List<Specialist> resultList = entityManager.createQuery(specialistCriteriaQuery).getResultList();
        resultList.forEach(specialist -> filterUserResponse.add(SpecialistMappers.convertToFilterDTO(specialist)));
        return filterUserResponse;
    }

    public Double getSpecialistRate(Long specialistId) {
        Optional<Specialist> specialist = findById(specialistId);
        if (specialist.isEmpty())
            throw new NotFoundException("this specialist does not exist!");
        return specialist.get().getStar();
    }

    public Long getSpecialistCredit(Long specialistId) {
        Optional<Specialist> specialist = findById(specialistId);
        if (specialist.isEmpty())
            throw new NotFoundException("this specialist does not exist!");
        return specialist.get().getCredit();
    }

    public List<Orders> findAllOrdersBySpecialist(OrderStatus status, Specialist specialist) {
        if (status == null)
            throw new NotFoundException("STATUS IS REQUIRED");
        return orderService.findAllBySpecialist(specialist, status);
    }

    public List<Offer> showAllOffersAccepted(OfferStatus status, Specialist specialist) {
        if (status == null)
            throw new NotFoundException("STATUS IS REQUIRED");
        return offerService.findOffersBySpecialistIdAndOfferStatus(specialist.getId(), status);
    }


    public Optional<Specialist> findById(Long id) {
        return specialistRepository.findById(id);
    }

    public Specialist save(Specialist specialist) {
        return specialistRepository.save(specialist);
    }

    public Optional<Specialist> findByEmail(String email) {
        return Optional.ofNullable(specialistRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(
                        String.format("USER %s NOT FOUND", email)
                )
        ));
    }


}