package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpecialistServiceTest {

    @Autowired
    private SpecialistService specialistService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OfferService offerService;


//-----------------------------SignUp_SignIn----------------------------------------------------------------------------


    @Test
    @Order(1)
    void signUpSpecialistWithDuplicateEmail() throws IOException {
        String pathImage = "D:\\0.jpg";
        byte[] imageData = Files.readAllBytes(Paths.get(pathImage));
        Specialist specialist = Specialist.builder()
                .firstName("Amir").lastName("mhmdzdh").role(Role.SPECIALIST).email("AmirM.ah@yahoo.com")
                .password("45#Po@iu").image(imageData).registrationTime(LocalDateTime.now())
                .credit(0L).star(0).status(SpecialistStatus.NEW)
                .build();
        assertThrows(DuplicateEmailException.class, () -> {
            specialistService.signUpSpecialist(specialist);
        });
    }

    @Test
    @Order(2)
    void signUpSpecialist() throws IOException {
        String pathImage = "D:\\0.jpg";
        byte[] imageData = Files.readAllBytes(Paths.get(pathImage));
        Specialist specialist = Specialist.builder()
                .firstName("Amir").lastName("mhmdzdh").role(Role.SPECIALIST).email("AmirM.ah@yahoo.com")
                .password("45#Po@iu").image(imageData).registrationTime(LocalDateTime.now())
                .credit(0L).star(0).status(SpecialistStatus.NEW)
                .build();
        specialistService.signUpSpecialist(specialist);
    }

    @Test
    @Order(3)
    void signInAdminWithIncorrectInfo() {
        String email = "AmirM.ah@yahoo.com";
        String password = "45#Po@iuu";
        assertThrows(NotFoundException.class, () -> {
            specialistService.signInSpecialist(email, password);
        });
    }
    @Test
    @Order(4)
    void signInSpecialist() {
        String email = "AmirM.ah@yahoo.com";
        String password = "45#Po@iu";

        Specialist specialist = specialistService.signInSpecialist(email, password);

        Assertions.assertEquals(email, specialist.getEmail());
        Assertions.assertEquals(password, specialist.getPassword());
    }
    @Test
    @Order(5)
    void changePassword(){


    }


    @Test
    @Order(5)
    void newOffer() {
        Offer offer = Offer.builder()
                .executionTime(LocalDateTime.of(2026, 2, 2, 2, 2))
                .endTime(LocalDateTime.of(2027, 3, 3, 3, 3))
                .sendTime(LocalDateTime.now())
                .proposedPrice(30000L)
                .build();
        Optional<Orders> ordersList = Optional.of(orderService.findAll().get(8));
        offer.setOrders(ordersList.get());
        Optional<Specialist> specialist = specialistService.findByEmail("milad.ah@yahoo.com");
        offer.setSpecialist(specialist.get());
        specialistService.newOffers(offer, specialist.get());
        assertEquals(1, offerService.findAll().size());
    }

    @Test
    @Order(6)
    void newOfferWithSpecialistNew() {
        assertThrows(SpecialistNoAccessException.class, () -> {
            Offer offer = Offer.builder()
                    .executionTime(LocalDateTime.of(2026, 2, 2, 2, 2))
                    .endTime(LocalDateTime.of(2027, 3, 3, 3, 3))
                    .sendTime(LocalDateTime.now())
                    .proposedPrice(30000L)
                    .build();
            Optional<Orders> ordersList = Optional.of(orderService.findAll().get(0));
            offer.setOrders(ordersList.get());
            Optional<Specialist> specialist = specialistService.findByEmail("milad.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.newOffers(offer, specialist.get());
            assertEquals(1, offerService.findAll().size());
        });
    }

    @Test
    @Order(7)
    void newOfferWithOrderIsNull() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Offer offer = Offer.builder()
                    .executionTime(LocalDateTime.of(2026, 2, 2, 2, 2))
                    .endTime(LocalDateTime.of(2027, 3, 3, 3, 3))
                    .sendTime(LocalDateTime.now())
                    .proposedPrice(30000L)
                    .build();
            Optional<Orders> ordersList = Optional.of(orderService.findAll().get(200));
            offer.setOrders(ordersList.get());
            Optional<Specialist> specialist = specialistService.findByEmail("milad.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.newOffers(offer, specialist.get());
            assertEquals(1, offerService.findAll().size());
        });
    }

    @Test
    @Order(8)
    void newOfferWithAccepted() {
        assertThrows(OrderStatusException.class, () -> {
            Offer offer = Offer.builder()
                    .executionTime(LocalDateTime.of(2026, 2, 2, 2, 2))
                    .endTime(LocalDateTime.of(2027, 3, 3, 3, 3))
                    .sendTime(LocalDateTime.now())
                    .proposedPrice(30000L)
                    .build();
            Optional<Orders> ordersList = Optional.of(orderService.findAll().get(200));
            offer.setOrders(ordersList.get());
            Optional<Specialist> specialist = specialistService.findByEmail("Honewdey.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.newOffers(offer, specialist.get());
            assertEquals(1, offerService.findAll().size());
        });
    }

    @Test
    @Order(9)
    void newOfferWithSpecialistDontJob() {
        assertThrows(SpecialistNoAccessException.class, () -> {
            Offer offer = Offer.builder()
                    .executionTime(LocalDateTime.of(2026, 2, 2, 2, 2))
                    .endTime(LocalDateTime.of(2027, 3, 3, 3, 3))
                    .sendTime(LocalDateTime.now())
                    .proposedPrice(30000L)
                    .build();
            Optional<Orders> ordersList = Optional.of(orderService.findAll().get(0));
            offer.setOrders(ordersList.get());
            Optional<Specialist> specialist = specialistService.findByEmail("Honewdey.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.newOffers(offer, specialist.get());
            assertEquals(1, offerService.findAll().size());
        });
    }

    @Test
    @Order(10)
    void newOfferAmountLessException() {
        assertThrows(AmountLessException.class, () -> {
            Offer offer = Offer.builder()
                    .executionTime(LocalDateTime.of(2026, 2, 2, 2, 2))
                    .endTime(LocalDateTime.of(2027, 3, 3, 3, 3))
                    .sendTime(LocalDateTime.now())
                    .proposedPrice(3L)
                    .build();
            Optional<Orders> ordersList = Optional.of(orderService.findAll().get(0));
            offer.setOrders(ordersList.get());
            Optional<Specialist> specialist = specialistService.findByEmail("amir.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.newOffers(offer, specialist.get());
            assertEquals(1, offerService.findAll().size());
        });
    }

    @Test
    @Order(11)
    void newOfferTimeLessException() {
        assertThrows(TimeException.class, () -> {
            Offer offer = Offer.builder()
                    .executionTime(LocalDateTime.of(2000, 2, 2, 2, 2))
                    .endTime(LocalDateTime.of(2027, 3, 3, 3, 3))
                    .sendTime(LocalDateTime.now())
                    .proposedPrice(30000L)
                    .build();
            Optional<Orders> ordersList = Optional.of(orderService.findAll().get(0));
            offer.setOrders(ordersList.get());
            Optional<Specialist> specialist = specialistService.findByEmail("amir.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.newOffers(offer, specialist.get());
            assertEquals(1, offerService.findAll().size());
        });
    }

    @Test
    @Order(12)
    void newOfferTimeAfterException() {
        assertThrows(TimeException.class, () -> {
            Offer offer = Offer.builder()
                    .executionTime(LocalDateTime.of(2026, 2, 2, 2, 2))
                    .endTime(LocalDateTime.of(2024, 3, 3, 3, 3))
                    .sendTime(LocalDateTime.now())
                    .proposedPrice(30000L)
                    .build();
            Optional<Orders> ordersList = Optional.of(orderService.findAll().get(0));
            offer.setOrders(ordersList.get());
            Optional<Specialist> specialist = specialistService.findByEmail("amir.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.newOffers(offer, specialist.get());
            assertEquals(1, offerService.findAll().size());
        });
    }


}