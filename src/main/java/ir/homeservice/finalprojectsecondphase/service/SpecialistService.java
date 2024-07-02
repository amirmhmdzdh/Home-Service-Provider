package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.OfferRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.SearchForUser;
import ir.homeservice.finalprojectsecondphase.dto.request.SpecialistRegisterRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.mapper.OfferMapper;
import ir.homeservice.finalprojectsecondphase.mapper.SpecialistMapper;
import ir.homeservice.finalprojectsecondphase.mapper.SpecialistMappers;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import ir.homeservice.finalprojectsecondphase.repository.SpecialistRepository;
import ir.homeservice.finalprojectsecondphase.utill.SaveImageToFile;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SpecialistService {
    @PersistenceContext
    private EntityManager entityManager;
    private final Validation validation;
    private final OfferService offerService;
    private final OrderService orderService;
    private final SpecialistRepository specialistRepository;

    public Specialist signUpSpecialist(SpecialistRegisterRequest specialist, String imagePath) {
        if (specialistRepository.findByEmail(specialist.email()).isPresent())
            throw new DuplicateInformationException("this Email already exist!");
        byte[] image = validation.checkImage(imagePath);
        SaveImageToFile.saveImageToFile(image, "D:\\test.jpg");
        Specialist specialist1 = Specialist.builder()
                .firstName(specialist.firstName()).lastName(specialist.lastName()).email(specialist.email())
                .status(SpecialistStatus.NEW).registrationTime(LocalDateTime.now()).image(image).credit(0L)
                .password(specialist.password()).role(Role.SPECIALIST).star(0d)
                .build();
        return specialistRepository.save(specialist1);
    }

    public Specialist signInSpecialist(String email, String password) {
        return specialistRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new NotFoundException("Specialist not found. "));
    }

    public Specialist changePasswordSpecialist(String email, String oldPassword, String newPassword) {
        Optional<Specialist> specialist = specialistRepository.findAll().stream()
                .filter(s -> email.equals(s.getEmail()) && oldPassword.equals(s.getPassword()))
                .findFirst();
        if (specialist.isEmpty()) {
            throw new NotFoundException("Invalid email or old password.");
        }
        Specialist specialist1 = specialist.get();
        specialist1.setPassword(newPassword);
        return save(specialist1);
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

        Optional.ofNullable(search.getFirstName())
                .map(firstName -> criteriaBuilder.equal(specialistRoot.get("firstName"), firstName))
                .ifPresent(predicateList::add);

        Optional.ofNullable(search.getLastName())
                .map(lastName -> criteriaBuilder.equal(specialistRoot.get("lastName"), lastName))
                .ifPresent(predicateList::add);

        Optional.ofNullable(search.getEmail())
                .map(email -> criteriaBuilder.equal(specialistRoot.get("email"), email))
                .ifPresent(predicateList::add);

        Optional.ofNullable(search.getIsActive())
                .map(isActive -> criteriaBuilder.equal(specialistRoot.get("isActive"), isActive))
                .ifPresent(predicateList::add);

        Optional.ofNullable(search.getUserStatus())
                .map(userStatus -> criteriaBuilder.equal(specialistRoot.get("userStatus"), userStatus))
                .ifPresent(predicateList::add);

//        if (search.getMinCredit() == 0 && search.getMaxCredit() != 0) {
//            search.setMinCredit(0L);
//        }
//        if (search.getMinCredit() != 0 && search.getMaxCredit() == 0) {
//            search.setMaxCredit(Long.MAX_VALUE);
//        }
//        if (search.getMinCredit() != 0 || search.getMaxCredit() != 0) {
//            Predicate creditPredicate = criteriaBuilder.between(specialistRoot.get("credit"),
//                    search.getMinCredit(), search.getMaxCredit());
//            predicateList.add(creditPredicate);
//        }


        if (search.getMinScore() == null && search.getMaxScore() != null)
            search.setMinScore(0.0);
        if (search.getMinScore() != null && search.getMaxScore() == null)
            search.setMaxScore(5.0);
        if (search.getMinScore() != null && search.getMaxScore() != null)
            predicateList.add(criteriaBuilder.between(specialistRoot.get("star"),
                    search.getMinScore(), search.getMaxScore()));

        if (search.getMinUserCreationAt() == null && search.getMaxUserCreationAt() != null) {
            search.setMinUserCreationAt(LocalDateTime.now().minusYears(1));
        }
        if (search.getMinUserCreationAt() != null && search.getMaxUserCreationAt() == null) {
            search.setMaxUserCreationAt(LocalDateTime.now());
        }
        if (search.getMinUserCreationAt() != null && search.getMaxUserCreationAt() != null) {
            predicateList.add(criteriaBuilder.between(specialistRoot.get("registrationTime"),
                    search.getMinUserCreationAt(), search.getMaxUserCreationAt()));
        }

        specialistCriteriaQuery.select(specialistRoot).where(criteriaBuilder.and(predicateList.toArray(new Predicate[0])));
        List<Specialist> resultList = entityManager.createQuery(specialistCriteriaQuery).getResultList();
        resultList.forEach(specialist -> filterUserResponse.add(SpecialistMappers.convertToFilterDTO(specialist)));
        return filterUserResponse;
    }

    public Optional<Specialist> findById(Long id) {
        return specialistRepository.findById(id);
    }

    public Optional<Specialist> findByEmail(String email) {
        return specialistRepository.findByEmail(email);
    }

    public Specialist save(Specialist specialist) {
        return specialistRepository.save(specialist);
    }


}