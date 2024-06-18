package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import ir.homeservice.finalprojectsecondphase.repository.SpecialistRepository;
import ir.homeservice.finalprojectsecondphase.utill.SaveImageToFile;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SpecialistService {
    private final OrderService orderService;
    private final OfferService offerService;
    private final Validation validation;
    private final SpecialistRepository specialistRepository;

    public void signUpSpecialist(Specialist specialist) {
        validation.checkEmail(specialist.getEmail());
        if (specialistRepository.findByEmail(specialist.getEmail()).isPresent())
            throw new DuplicateEmailException("this Email already exist!");
        validation.checkPassword(specialist.getPassword());
        validation.checkText(specialist.getFirstName());
        validation.checkText(specialist.getLastName());
        validation.checkImage(specialist.getImage());
        SaveImageToFile.saveImageToFile(specialist.getImage(), "D:\\test.jpg");
        specialistRepository.save(specialist);
    }

    public Specialist signInSpecialist(String email, String password) {
        validation.checkEmail(email);
        validation.checkPassword(password);
        return specialistRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new NotFoundException("Specialist not found. "));
    }

    public void newOffers(Offer offer, Specialist specialist) {
        validation.checkPositiveNumber(offer.getOrders().getId());
        validation.checkPositiveNumber(offer.getProposedPrice());
        Optional<Specialist> specialistOptional = specialistRepository.findById(specialist.getId());
        if (!specialistOptional.get().getStatus().equals(SpecialistStatus.CONFIRMED))
            throw new SpecialistNoAccessException("the status of specialist is not CONFIRMED");
        Optional<Orders> ordersOptional = orderService.findById(offer.getOrders().getId());
        OrderStatus orderStatus = ordersOptional.get().getOrderStatus();
        if (!(orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION) ||
                orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION)))
            throw new OrderStatusException("this order has already accepted the offer");
        if (!(specialistOptional.get().getSubServicesList().contains(ordersOptional.get().getSubServices())))
            throw new SpecialistNoAccessException("this specialist does not have such job title!");
        if (ordersOptional.get().getProposedPrice() > offer.getProposedPrice())
            throw new AmountLessException("the proposed-price should not be lower than the order proposed-price!");
        if (offer.getExecutionTime().isBefore(ordersOptional.get().getExecutionTime()))
            throw new TimeException("no order has been in your proposed time for begin job!");
        if (offer.getEndTime().isBefore(offer.getExecutionTime()))
            throw new TimeException("time does not go back!");
        offer.setSpecialist(specialistOptional.get());
        offer.setOrders(ordersOptional.get());
        offer.setOfferStatus(OfferStatus.WAITING);
        offerService.save(offer);
        if (orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION))
            ordersOptional.get().setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION);
        orderService.save(ordersOptional.get());
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        validation.checkEmail(email);
        validation.checkPassword(oldPassword);
        validation.checkPassword(newPassword);
        Optional<Specialist> specialist = specialistRepository.findAll().stream()
                .filter(s -> email.equals(s.getEmail()) && oldPassword.equals(s.getPassword()))
                .findFirst();
        if (specialist.isEmpty()) {
            throw new DuplicateEmailException("Invalid email or old password.");
        }
        Specialist specialist1 = specialist.get();
        specialist1.setPassword(newPassword);
        save(specialist1);
    }


    public Optional<Specialist> findById(Long id) {
        return specialistRepository.findById(id);
    }

    public void save(Specialist specialist) {
        specialistRepository.save(specialist);
    }

    public Optional<Specialist> findByEmail(String email) {
        return specialistRepository.findByEmail(email);
    }

}