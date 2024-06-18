package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Admin;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import ir.homeservice.finalprojectsecondphase.repository.AdminRepository;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final MainServiceService mainServiceService;
    private final SubServiceService subServiceService;
    private final SpecialistService specialistService;
    private final AdminRepository adminRepository;
    private final Validation validation;


    public Admin signInAdmin(String email, String password) {
        return adminRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new NotFoundException("This admin does not exist!"));
    }


    public void createMainService(MainService mainService) {
        validation.checkText(mainService.getName());
        if (mainServiceService.findByName(mainService.getName()).isPresent())
            throw new MainServicesIsExistException("this main service already exist!");
        MainService mainService1 = MainService.builder()
                .name(mainService.getName())
                .build();
        mainServiceService.save(mainService1);
    }

    public void createSubService(SubService subService) {
        MainService mainService = subService.getMainService();
        String subServiceName = subService.getName();
        validation.checkText(mainService.getName());
        validation.checkText(subServiceName);
        validation.checkBlank(subService.getDescription());
        Optional<MainService> mainServiceOptional = mainServiceService.findByName(mainService.getName());
        if (mainServiceOptional.isEmpty())
            throw new MainServicesIsExistException("this main service dose not exist!");
        if (subServiceService.findByName(subServiceName).isPresent())
            throw new SubServicesIsExistException("this subService already exist!");
        MainService mainService1 = mainServiceOptional.get();
        subService.setMainService(mainService1);
        subServiceService.save(subService);
    }

    public void updateSubService(SubService upateSubService) {
        validation.checkText(upateSubService.getName());
        Optional<SubService> existingSubService = subServiceService.findById(upateSubService.getId());
        if (existingSubService.isEmpty())
            throw new SubServicesIsNotExistException("this subServices dose not exist!");
        SubService service = existingSubService.get();
        service.setName(upateSubService.getName());
        validation.checkText(upateSubService.getDescription());
        service.setDescription(upateSubService.getDescription());
        validation.checkPositiveNumber(upateSubService.getBasePrice());
        service.setBasePrice(upateSubService.getBasePrice());
        subServiceService.save(existingSubService.get());
    }

    public void confirmSpecialist(Long specialistId) {
        validation.checkPositiveNumber(specialistId);
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new SpecialistIsNotExistException("This specialist does not exist!"));
        if (specialist.getStatus().equals(SpecialistStatus.CONFIRMED))
            throw new SpecialistIsHoldsExistException("this specialist is currently certified!");
        specialist.setStatus(SpecialistStatus.CONFIRMED);
        specialistService.save(specialist);
    }

    public void addSpecialistToSubService(Long subServiceId, Long specialistId) {
        validation.checkPositiveNumber(subServiceId);
        validation.checkPositiveNumber(specialistId);
        SubService subService = subServiceService.findById(subServiceId)
                .orElseThrow(() -> new SubServicesIsNotExistException("This subService does not exist!"));
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new SpecialistIsNotExistException("This specialist does not exist!"));
        if (specialist.getStatus() != SpecialistStatus.CONFIRMED)
            throw new SpecialistNoAccessException("The status of the specialist is not CONFIRMED");
        if (specialist.getSubServicesList().contains(subService))
            throw new DuplicateSubServiceException("the subService is Duplicate");
        specialist.addSubServices(subService);
        specialistService.save(specialist);
    }

    public void deleteSubServicesFromSpecialist(Long subServiceId, Long specialistId) {
        validation.checkPositiveNumber(subServiceId);
        validation.checkPositiveNumber(specialistId);
        SubService subService = subServiceService.findById(subServiceId)
                .orElseThrow(() -> new SubServicesIsNotExistException("This subService does not exist!"));
        Specialist specialist = specialistService.findById(specialistId)
                .orElseThrow(() -> new SpecialistIsNotExistException("This specialist does not exist!"));
        specialist.deleteSubServices(subService);
        specialistService.save(specialist);
    }
}

