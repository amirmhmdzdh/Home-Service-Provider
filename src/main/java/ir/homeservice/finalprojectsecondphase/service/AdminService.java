package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.SearchForUser;
import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Admin;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import ir.homeservice.finalprojectsecondphase.repository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final CustomerService customerService;
    private final AdminRepository adminRepository;
    private final SpecialistService specialistService;
    private final SubServiceService subServiceService;
    private final MainServiceService mainServiceService;

    public Admin signInAdmin(String email, String password) {
        return adminRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new NotFoundException("This admin does not exist!"));
    }


    public MainService createMainService(MainService mainService) {
        if (mainServiceService.findByName(mainService.getName()).isPresent())
            throw new DuplicateInformationException("this " + mainService.getName() + " main service already exist!");
        MainService mainService1 = MainService.builder()
                .name(mainService.getName())
                .build();
        return mainServiceService.save(mainService1);
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
                .name(subService.getName())
                .basePrice(subService.getBasePrice())
                .description(subService.getDescription())
                .mainService(mainService1)
                .build();
        return subServiceService.save(subService1);
    }

    public SubService updateSubService(SubService updateSubService) {
        Optional<SubService> serviceServiceById = subServiceService.findById(updateSubService.getId());
        if (serviceServiceById.isEmpty())
            throw new NotFoundException("this subServices dose not exist!");
        SubService service = serviceServiceById.get();
        service.setName(updateSubService.getName());
        service.setDescription(service.getDescription());
        service.setBasePrice(service.getBasePrice());
        return subServiceService.save(serviceServiceById.get());
    }


    public void confirmSpecialist(Long specialistId) {
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("This specialist does not exist!"));
        if (specialist.getStatus().equals(SpecialistStatus.CONFIRMED))
            throw new DuplicateInformationException("this specialist is currently certified!");
        specialist.setStatus(SpecialistStatus.CONFIRMED);
        specialistService.save(specialist);
    }

    public void addSpecialistToSubService(Long subServiceId, Long specialistId) {
        SubService subService = subServiceService.findById(subServiceId)
                .orElseThrow(() -> new NotFoundException("This subService does not exist!"));
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("This specialist does not exist!"));
        if (specialist.getStatus() != SpecialistStatus.CONFIRMED)
            throw new NotFoundException("The status of the specialist is not CONFIRMED");
        if (specialist.getSubServicesList().contains(subService))
            throw new DuplicateInformationException("the subService is Duplicate");
        specialist.addSubServices(subService);
        specialistService.save(specialist);
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
}

