package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.SubServiceRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.UpdateSubServiceRequest;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final MainServiceService mainServiceService;
    private final SubServiceService subServiceService;
    private final SpecialistService specialistService;
    private final AdminRepository adminRepository;


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

    public SubService createSubService(SubServiceRequest subService) {

        String mainServiceName = subService.mainService().name();
        String subServiceName = subService.name();
        Optional<MainService> mainServiceOptional = mainServiceService.findByName(mainServiceName);
        if (mainServiceOptional.isEmpty())
            throw new NotFoundException("this main service dose not exist!");

        if (subServiceService.findByName(subServiceName).isPresent())
            throw new NotFoundException("this subService already exist!");
        MainService mainService1 = mainServiceOptional.get();
        SubService subService1 = SubService.builder()
                .name(subServiceName)
                .basePrice(subService.basePrice())
                .description(subService.description())
                .mainService(mainService1)
                .build();
        return subServiceService.save(subService1);
    }

    public SubService updateSubService(UpdateSubServiceRequest updateSubService) {
        Optional<SubService> serviceServiceById = subServiceService.findById(updateSubService.subServicesId());
        if (serviceServiceById.isEmpty())
            throw new NotFoundException("this subServices dose not exist!");
        SubService service = serviceServiceById.get();
        service.setName(updateSubService.name());
        service.setDescription(updateSubService.description());
        service.setBasePrice(updateSubService.basePrice());
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
}

