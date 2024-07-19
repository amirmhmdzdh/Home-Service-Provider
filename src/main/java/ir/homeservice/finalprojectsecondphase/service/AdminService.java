package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.AdminRegisterRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.OrderHistoryDto;
import ir.homeservice.finalprojectsecondphase.dto.request.SearchForUser;
import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.dto.response.ReportDto;
import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Admin;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import ir.homeservice.finalprojectsecondphase.repository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor

public class AdminService {
    private final OrderService orderService;
    private final UsersService usersService;
    private final CustomerService customerService;
    private final AdminRepository adminRepository;
    private final SpecialistService specialistService;
    private final SubServiceService subServiceService;
    private final MainServiceService mainServiceService;
    private final BCryptPasswordEncoder passwordEncoder;


    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(String.format(
                        "USER %s NOT FOUND", email
                ))
        );
    }

    public Admin saveAdmin(AdminRegisterRequest request) {
        if (!adminRepository.existsByEmail(request.email())) {
            Admin admin = Admin.builder()
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .email(request.email())
                    .role(Role.ADMIN)
                    .isActive(true)
                    .password(passwordEncoder.encode(request.password()))
                    .registrationTime(LocalDateTime.now())
                    .build();
            return adminRepository.save(admin);
        }
        return null;
    }

    public MainService createMainService(MainService mainService) {
        if (mainServiceService.findByName(mainService.getName()).isPresent())
            throw new DuplicateInformationException("THIS " + mainService.getName() + " SERVICE ALREADY EXISTS! ");
        mainService.setRegistrationTime(LocalDateTime.now());
        return mainServiceService.save(mainService);
    }

    public SubService createSubService(SubService subService) {

        MainService mainService = subService.getMainService();
        Optional<MainService> mainServiceOptional = mainServiceService.findByName(mainService.getName());
        if (mainServiceOptional.isEmpty())
            throw new NotFoundException("this main service dose not exist!");

        if (subServiceService.findByName(subService.getName()).isPresent())
            throw new NotFoundException("this subService already exist!");
        MainService mainService1 = mainServiceOptional.get();
        SubService subService1 = SubService.builder()
                .name(subService.getName()).basePrice(subService.getBasePrice())
                .description(subService.getDescription()).registrationTime(LocalDateTime.now())
                .mainService(mainService1).build();
        return subServiceService.save(subService1);
    }

    public SubService updateSubService(SubService updateSubService) {
        Optional<SubService> serviceServiceById = subServiceService.findById(updateSubService.getId());
        if (serviceServiceById.isEmpty())
            throw new NotFoundException("this subServices dose not exist!");
        SubService service = serviceServiceById.get();
        service.setName(updateSubService.getName());
        service.setDescription(updateSubService.getDescription());
        service.setBasePrice(updateSubService.getBasePrice());
        return subServiceService.save(service);
    }


    public Specialist confirmSpecialist(Long specialistId) {
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("This specialist does not exist!"));
        if (specialist.getStatus().equals(SpecialistStatus.CONFIRMED))
            throw new DuplicateInformationException("this specialist is currently certified!");
        specialist.setStatus(SpecialistStatus.CONFIRMED);
        return specialistService.save(specialist);
    }

    public Specialist addSpecialistToSubService(Long subServiceId, Long specialistId) {
        SubService subService = subServiceService.findById(subServiceId)
                .orElseThrow(() -> new NotFoundException("This subService does not exist!"));
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("This specialist does not exist!"));
        if (specialist.getStatus() != SpecialistStatus.CONFIRMED)
            throw new NotFoundException("The status of the specialist is not CONFIRMED");
        if (specialist.getSubServicesList().contains(subService))
            throw new DuplicateInformationException("the subService is Duplicate");
        specialist.addSubServices(subService);
        return specialistService.save(specialist);
    }

    public void deleteSubServicesFromSpecialist(Long subServiceId, Long specialistId) {
        SubService subService = subServiceService.findById(subServiceId)
                .orElseThrow(() -> new NotFoundException("This subService does not exist!"));
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("This specialist does not exist!"));
        specialist.deleteSubServices(subService);
        specialistService.save(specialist);
    }

    public List<FilterUserResponse> searchUser(SearchForUser search) {
        List<FilterUserResponse> filterUserResponseList = new ArrayList<>();

        switch (search.getUserType()) {
            case "CUSTOMER" -> filterUserResponseList.addAll(customerService.customerFilter(search));
            case "SPECIALIST" -> filterUserResponseList.addAll(specialistService.specialistFilter(search));
            case "ALL" -> {
                filterUserResponseList.addAll(customerService.customerFilter(search));
                filterUserResponseList.addAll(specialistService.specialistFilter(search));
            }
        }
        return filterUserResponseList;
    }

    public List<SubService> getHistoryOfSubServicesForUser(String email) {
        return subServiceService.historyOfSubServicesForCurrentUser(email);
    }

    public List<Orders> getHistoryOfOrdersForUser(OrderHistoryDto dto) {
        return orderService.historyOfOrdersForUser(dto);
    }

    public ReportDto reportingFromUsers(String email) {
        ReportDto report = new ReportDto();
        if (usersService.findByEmail(email).get().getRole().equals(Role.SPECIALIST)) {
            LocalDateTime creationDate = specialistService.findByEmail(email).get().getRegistrationTime();
            report.setCreationDate(creationDate);
            Long countedOfOrders = orderService.countOfOrders(email);
            report.setDoneOrders(countedOfOrders);
        }
        if (usersService.findByEmail(email).get().getRole().equals(Role.CUSTOMER)) {
            LocalDateTime creationDate = customerService.findByEmail(email).get().getRegistrationTime();
            report.setCreationDate(creationDate);
            Long countedOfOrders = orderService.countOfOrders(email);
            report.setRequestOfOrders(countedOfOrders);
        }
        return report;
    }

    public List<MainService> findAllMainService() {
        return mainServiceService.findAllMainService();
    }

    public List<SubService> findAllSubService() {
        return subServiceService.findAllSubService();
    }
}