package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Admin;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminServiceTest {

    @Autowired
    private AdminService adminService;
    @Autowired
    private MainServiceService mainServiceService;
    @Autowired
    private SubServiceService subServiceService;
    @Autowired
    private SpecialistService specialistService;

//-----------------------------SignUp_SignIn----------------------------------------------------------------------------

    @Test
    @Order(1)
    void signInAdmin() {
        String email = "Admin";
        String pass = "Admin";

        Admin admin = adminService.signInAdmin(email, pass);

        assertEquals(email, admin.getEmail());
        assertEquals(pass, admin.getPassword());
    }

    @Test
    @Order(2)
    void signInAdminWithIncorrectInfo() {
        String email = "Admin";
        String pass = "Admin1";
        assertThrows(NotFoundException.class, () -> {
            adminService.signInAdmin(email, pass);
        });
    }
    //-----------------------------Create MainService AND SubService----------------------------------------------------

    @Test
    @Order(3)
    void createNewMainServiceWithExceptionForDuplicate() {
        MainService mainService = MainService.builder()
                .name("Building")
                .build();
        assertThrows(MainServicesIsExistException.class, () -> {
            adminService.createMainService(mainService);
        }, "Expected MainServicesIsExistException to be thrown");
    }


    @Test
    @Order(4)
    void createMainService() {
        adminService.createMainService(new MainService("Building"));
        Optional<MainService> optionalMainService =
                mainServiceService.findByName("Building");
        assertEquals("Building", optionalMainService.get().getName());
    }

    @Test
    @Order(5)
    void mainServicesIsExistException() {
        assertThrows(MainServicesIsExistException.class, () -> {
            adminService
                    .createSubService(new SubService("barge", 100L,
                            "test", new MainService("Build")));
        });
    }

    @Test
    @Order(6)
    void addDuplicateSubServices() {
        assertThrows(SubServicesIsExistException.class, () -> {
            adminService
                    .createSubService(new SubService("barge", 100L,
                            "test", new MainService("Building")));
        });
    }

    @Test
    @Order(7)
    void createSubService() {
        adminService.createSubService(
                new SubService("barge", 100L, "test", new MainService("Building")));
        Optional<SubService> serviceByName = subServiceService.findByName("barge");
        assertEquals(100L, serviceByName.get().getBasePrice());
    }


    //---------------------------------Update SubService----------------------------------------------------------------

    @Test
    @Order(8)
    void SubServicesIsNotExistException() {
        SubService subService = SubService.builder()
                .id(10L)
                .name("aaa")
                .basePrice(100L)
                .description("hardWork")
                .build();

        assertThrows(SubServicesIsNotExistException.class, () -> {
            adminService.updateSubService(subService);
        });
    }

    @Test
    @Order(9)
    void updateSubService() {
        SubService subServices = subServiceService.findByName("black").get();
        SubService subService = SubService.builder()
                .id(subServices.getId())
                .name("barge")
                .basePrice(100L)
                .description("hardWork")
                .build();
        adminService.updateSubService(subService);
        SubService newSubServices = subServiceService.findByName("barge").get();
        assertEquals(subServices.getId(), newSubServices.getId());
    }


    //---------------------------------Confirm Specialist---------------------------------------------------------------
    @Test
    @Order(10)
    void SpecialistNotFoundException() {
        assertThrows(SpecialistIsNotExistException.class, () -> {
            Specialist specialist = Specialist.builder()
                    .id(100L)
                    .build();
            adminService.confirmSpecialist(specialist.getId());
        });
    }

    @Test
    @Order(11)
    void SpecialistIsHoldsExistException() {
        assertThrows(SpecialistIsHoldsExistException.class, () -> {
            Optional<Specialist> specialist = specialistService.findByEmail("AmirM.ah@yahoo.com");
            assertTrue(specialist.isPresent());
            adminService.confirmSpecialist(specialist.get().getId());
        });
    }

    @Test
    @Order(12)
    void confirmSpecialist() {
        Optional<Specialist> specialist = specialistService.findByEmail("AmirM.ah@yahoo.com");
        adminService.confirmSpecialist(specialist.get().getId());
        Specialist updateSpecialist = specialistService.findByEmail("AmirM.ah@yahoo.com").get();
        assertEquals(updateSpecialist.getStatus(), SpecialistStatus.CONFIRMED);
    }

    //---------------------------------Add Specialist to SubService-----------------------------------------------------

    @Test
    @Order(13)
    void testAddSpecialistToSubService() {
        Optional<Specialist> specialistOptional = specialistService.findByEmail("AmirM.ah@yahoo.com");
        Optional<SubService> subServiceOptional = subServiceService.findByName("barge");

        assertTrue(specialistOptional.isPresent(), "Specialist not found");
        assertTrue(subServiceOptional.isPresent(), "Sub-service not found");

        Specialist specialist = specialistOptional.get();
        SubService subService = subServiceOptional.get();

        adminService.addSpecialistToSubService(subService.getId(), specialist.getId());


        SubService retrievedSubService = subServiceService.findByName("barge").get();
        // assertTrue(specialist.getSubServicesList().stream().anyMatch(sub -> sub.getId().equals(retrievedSubService.getId())), "Sub-service not associated with specialist");
        assertEquals(retrievedSubService.getId(), subService.getId(), "Retrieved sub-service ID does not match");

    }

    @Test
    @Order(14)
    void subServicesIsNotExistExceptions() {
        SubService subService = SubService.builder().id(100L).build();
        Specialist specialist = Specialist.builder().id(100L).build();
        assertThrows(SubServicesIsNotExistException.class, () -> {
            adminService.addSpecialistToSubService(subService.getId(), specialist.getId());
        });
    }

    @Test
    @Order(15)
    void specialistIsNotExistExceptions() {
        SubService subService = SubService.builder().id(1L).build();
        Specialist specialist = Specialist.builder().id(100L).build();
        assertThrows(SpecialistIsNotExistException.class, () -> {
            adminService.addSpecialistToSubService(subService.getId(), specialist.getId());
        });
    }

    @Test
    @Order(16)
    void specialistIsNotConfirm() {
        Optional<SubService> serviceByName = subServiceService.findByName("barge");
        Optional<Specialist> specialist = specialistService.findByEmail("AmirM.ah@yahoo.com");
        assertThrows(SpecialistNoAccessException.class, () -> {
            adminService.addSpecialistToSubService(serviceByName.get().getId(), specialist.get().getId());
        });
    }

    @Test
    @Order(17)
    void subServiceDuplicate() {
        Optional<SubService> serviceByName = subServiceService.findByName("barge");
        Optional<Specialist> specialist = specialistService.findByEmail("AmirM.ah@yahoo.com");
        assertThrows(DuplicateSubServiceException.class, () -> {
            adminService.addSpecialistToSubService(serviceByName.get().getId(), specialist.get().getId());
        });

    }


    //---------------------------------delete Specialist to SubService-----------------------------------------------------

    @Test
    @Order(18)
    void subServicesIsNotExistExceptionsForDelete() {
        SubService subService = SubService.builder().id(100L).build();
        Specialist specialist = Specialist.builder().id(100L).build();
        assertThrows(SubServicesIsNotExistException.class, () -> {
            adminService.deleteSubServicesFromSpecialist(subService.getId(), specialist.getId());
        });
    }

    @Test
    @Order(19)
    void specialistIsNotExistExceptionsForDelete() {
        SubService subService = SubService.builder().id(1L).build();
        Specialist specialist = Specialist.builder().id(100L).build();
        assertThrows(SpecialistIsNotExistException.class, () -> {
            adminService.deleteSubServicesFromSpecialist(subService.getId(), specialist.getId());
        });
    }

    @Test
    @Order(20)
    void deleteSubServicesFromSpecialist() {
        Optional<Specialist> serviceByEmail = specialistService.findByEmail("AmirM.ah@yahoo.com");
        Assertions.assertTrue(serviceByEmail.isPresent(), "Specialist should exist with the given email");
        Optional<SubService> subService = subServiceService.findByName("barge");
        Assertions.assertTrue(subService.isPresent(), "Sub-service should exist with the given name");
        adminService.deleteSubServicesFromSpecialist(subService.get().getId(), serviceByEmail.get().getId());
        Optional<SubService> deletedSubService = subServiceService.findByName("barge");
        Assertions.assertFalse(serviceByEmail.get().getSubServicesList().contains(deletedSubService));
    }
}