package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.address.Address;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerServiceTest {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SubServiceService subServiceService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private SpecialistService specialistService;

    //-------------------------------SignUP & SignIn Customer-----------------------------------------------------------
    @Test
    @Order(1)
    void signUpCustomer() {
        Customer customer = Customer.builder()
                .firstName("omid")
                .lastName("hassani")
                .email("omidiii@gmail.com")
                .password("ASvSD@#423")
                .registrationTime(LocalDateTime.now())
                .role(Role.CUSTOMER)
                .credit(10000L)
                .build();
        customerService.signUpCustomer(customer);
        Optional<Customer> findEmail = customerService.findByEmail("omidiii@gmail.com");
        Address address = new Address("Tehran", "valiiasr", "koche1", "asa12", findEmail.get());
        customerService.addAddress(address, findEmail.get());
        Assertions.assertTrue(findEmail.isPresent());
        Assertions.assertEquals(customer.getEmail(), findEmail.get().getEmail());
    }

    @Test
    @Order(2)
    void signUpDuplicatedCustomer() {
        Customer customer = Customer.builder()
                .firstName("ali")
                .lastName("mhmdzdh")
                .email("AliMhmd@gmail.com")
                .password("ASvSD@#423")
                .registrationTime(LocalDateTime.now())
                .role(Role.CUSTOMER)
                .credit(10000L)
                .build();
        Assertions.assertThrows(DuplicateEmailException.class, () -> {
            customerService.signUpCustomer(customer);
        });

    }

    @Test
    @Order(3)
    void signInCustomer() {
        String email = "AliMhmd@gmail.com";
        String pass = "ASvSD@#4234";
        Customer customer = customerService.signInCustomer(email, pass);
        Assertions.assertEquals(email, customer.getEmail());
        Assertions.assertEquals(pass, customer.getPassword());
    }

    @Test
    @Order(4)
    void signInCustomerWithIncorrectInfo() {
        String email = "AliMhmd@gmail.com";
        String pass = "ASvSD@#4234";
        assertThrows(CustomerStatusException.class, () -> {
            customerService.signInCustomer(email, pass);
        });
    }

    //-----------------------------------change Password -------------------------------------------------------------------
    @Test
    @Order(5)
    void changePassword() {
        customerService.changePassword("AliMhmd@gmail.com", "ASvSD@#13801125", "ASvSD@#4234");
        Optional<Customer> customer = customerService.findByEmail("AliMhmd@gmail.com");
        Assertions.assertEquals(customer.get().getPassword(), "ASvSD@#4234");
    }

    @Test
    @Order(6)
    void incorrectInfo() {
        Assertions.assertThrows(DuplicateEmailException.class, () -> {
            customerService.changePassword("weq@gmail.com", "12345!qQwe", "12345!qQ12");
        });
    }

    //------------------------------------watch And Order-------------------------------------------------------------------
    @Test
    @Order(7)
    void watchAndOrder() {
        Customer customer = customerService.findByEmail("AliMhmd@gmail.com").get();
        SubService subService = subServiceService.findByName("bargers").get();

        Orders orders = Orders.builder()
                .orderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION)
                .proposedPrice(150L)
                .customer(customer)
                .executionTime(LocalDateTime.of(2025, 12, 12, 12, 12))
                .endTime(LocalDateTime.of(2025, 12, 19, 12, 12))
                .subServices(subService)
                .address(customer.getAddressList().get(0))
                .description("testOrder")
                .build();
        customerService.watchAndOrder(orders, customer.getId());
        Assertions.assertEquals(
                orderService.findByCustomerEmailAndOrderStatus("AliMhmd@gmail.com",
                        OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION).get(0).getDescription(), "testOrder");
    }

    @Test
    @Order(8)
    void watchAndOrderWithFakeCustomer() {
        Customer customer = Customer.builder().id(500L).build();
        SubService subService = SubService.builder().id(1L).build();
        Orders orders = Orders.builder()
                .orderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION)
                .proposedPrice(150L)
                .customer(customer)
                .executionTime(LocalDateTime.of(2025, 12, 12, 12, 12))
                .endTime(LocalDateTime.of(2025, 12, 19, 12, 12))
                .subServices(subService)
                .description("testOrder")
                .build();
        Assertions.assertThrows(CustomerStatusException.class, () -> {
            customerService.watchAndOrder(orders, customer.getId());
        });
    }

    @Test
    @Order(9)
    void watchAndOrderWithFakeInfo() {
        Customer customer = Customer.builder().id(1L).build();
        SubService subService = SubService.builder().id(10L).name("hava").build();
        Orders orders = Orders.builder()
                .orderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION)
                .proposedPrice(150L)
                .customer(customer)
                .executionTime(LocalDateTime.of(2025, 12, 12, 12, 12))
                .endTime(LocalDateTime.of(2025, 12, 19, 12, 12))
                .subServices(subService)
                .description("testOrder")
                .build();
        Assertions.assertThrows(SubServicesIsNotExistException.class, () -> {
            customerService.watchAndOrder(orders, customer.getId());
        });
    }

    //----------------------------------trackOrders-------------------------------------------------------------------------

    @Test
    @Order(10)
    void findOfferListByProposedPrice() {
        Customer customer = customerService.findByEmail("AliMhmd@gmail.com").get();
        List<Offer> offerList =
                customerService.findOfferListByProposedPrice(customer.getOrdersList().get(0).getId(), customer);
        assertNotNull(offerList);
    }

    @Test
    @Order(11)
    void findOfferListBySpecialistScore() {
        Customer customer = customerService.findByEmail("AliMhmd@gmail.com").get();
        Specialist specialist = specialistService.findByEmail("AmirM.ah@yahoo.com").get();
        specialist.setStar(8);
        specialistService.save(specialist);
        List<Offer> offerListBySpecialistScore = customerService
                .findOfferListBySpecialistScore(customer.getOrdersList().get(0).getId(), customer);
        assertNotNull(offerListBySpecialistScore);
    }

    @Test
    @Order(12)
    void newTrackOrders() {
        Customer customer = customerService.findByEmail("AliMhmd@gmail.com").get();
        Long id = 6L;
        customerService.trackOrders(id, customer);
        Optional<Offer> optionalOffer = offerService.findById(id);
        Assertions.assertEquals(OrderStatus.WAITING_FOR_SPECIALIST_TO_COME,
                optionalOffer.get().getOrders().getOrderStatus());
    }

    @Test
    @Order(13)
    void trackOrdersWithExist() {
        Customer customer = customerService.findByEmail("AliMhmd@gmail.com").get();
        Long id = 6L;
        Assertions.assertThrows(OfferStatusException.class, () -> {
            customerService.trackOrders(id, customer);
        });
    }

    //--------------------------------notificationOfStatus------------------------------------------------------------------
    @Test
    @Order(14)
    void notificationOfStatus() {
        Customer customer = customerService.findByEmail("AliMhmd@gmail.com").get();
        Long id = orderService.findAll().get(0).getId();
        customerService.notificationOfStatus(id, customer);
        Assertions.assertEquals(orderService.findById(id).get().getOrderStatus(), OrderStatus.DONE);
    }

    @Test
    @Order(15)
    void orderIsNotExistException() {
        Customer customer = customerService.findByEmail("AliMhmd@gmail.com").get();
        Long id = orderService.findAll().get(0).getId();
        Assertions.assertThrows(OrderIsNotExistException.class, () -> {
            customerService.notificationOfStatus(id, customer);
        });
    }
}
