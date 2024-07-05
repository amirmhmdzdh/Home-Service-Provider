package ir.homeservice.finalprojectsecondphase.controller;

import ir.homeservice.finalprojectsecondphase.dto.request.*;
import ir.homeservice.finalprojectsecondphase.dto.response.*;
import ir.homeservice.finalprojectsecondphase.mapper.AdminLoginMapper;
import ir.homeservice.finalprojectsecondphase.mapper.MainServiceMapper;
import ir.homeservice.finalprojectsecondphase.mapper.SubServiceMapper;
import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Admin;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final ModelMapper modelMapper;
    private final AdminService adminService;


    @GetMapping("/login-admin")
    public ResponseEntity<UserResponseToLogin> signInAdmin(@RequestBody UserRequestToLogin request) {

        Admin admin = AdminLoginMapper.INSTANCE.signInAdminRequestToModel(request);

        Admin signInAdmin = adminService.signInAdmin(admin.getEmail(), admin.getPassword());

        UserResponseToLogin userResponseToLogin = modelMapper.map(signInAdmin, UserResponseToLogin.class);

        return new ResponseEntity<>(userResponseToLogin, HttpStatus.FOUND);
    }

    @PostMapping("/create-main-service")
    public ResponseEntity<MainServiceResponse> createMainService(@Valid @RequestBody MainServiceRequest request) {

        MainService mappedMain = MainServiceMapper.INSTANCE.mainServiceSaveRequestToModel(request);

        MainService savedMain = adminService.createMainService(mappedMain);

        return new ResponseEntity<>(MainServiceMapper.INSTANCE.modelToMainServiceSaveResponse(savedMain),
                HttpStatus.CREATED);
    }

    @PostMapping("/add-subServices")
    public ResponseEntity<SubServiceResponse> addSubServices(@Valid @RequestBody SubServiceRequest request) {

        SubService requestToModel = SubServiceMapper.INSTANCE.subServiceSaveRequestToModel(request);

        SubService subService = adminService.createSubService(requestToModel);

        SubServiceResponse subServiceResponse = modelMapper.map(subService, SubServiceResponse.class);

        return new ResponseEntity<>(subServiceResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update-SubService")
    public ResponseEntity<SubServiceResponse> updateSubServices(@Valid @RequestBody UpdateSubServiceRequest request) {

        SubService requestToModel = SubServiceMapper.INSTANCE.UpdateSubServiceRequestToModel(request);

        SubService subService = adminService.updateSubService(requestToModel);

        SubServiceResponse subServiceResponse = modelMapper.map(subService, SubServiceResponse.class);

        return new ResponseEntity<>(subServiceResponse, HttpStatus.OK);

    }

    @PutMapping("/confirm-Specialist/{specialistId}")
    public ResponseEntity<SpecialistResponseRegister> confirmSpecialist(@PathVariable Long specialistId) {

        Specialist specialist = adminService.confirmSpecialist(specialistId);

        SpecialistResponseRegister map = modelMapper.map(specialist, SpecialistResponseRegister.class);

        return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
    }

    @PutMapping("/add-Specialist-To-SubService/{subServiceId}/{specialistId}")
    public ResponseEntity<SpecialistResponseRegister> addSpecialistToSubService(
            @PathVariable Long subServiceId, @PathVariable Long specialistId) {

        Specialist specialist = adminService.addSpecialistToSubService(subServiceId, specialistId);

        SpecialistResponseRegister map = modelMapper.map(specialist, SpecialistResponseRegister.class);

        return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete-SubServices-From-Specialist/{subServiceId}/{specialistId}")
    public ResponseEntity<String> deleteSubServicesFromSpecialist(
            @PathVariable Long subServiceId, @PathVariable Long specialistId) {
        adminService.deleteSubServicesFromSpecialist(subServiceId, specialistId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/search")
    public List<FilterUserResponse> search(@RequestBody SearchForUser search) {
        return adminService.searchUser(search);
    }

    @GetMapping("/show-all-MainService")
    public List<MainService> findAllMainServices() {
        return adminService.findAllMainService();
    }

    @GetMapping("/show-all-SubService")
    public List<SubService> findAllSubServices() {
        return adminService.findAllSubService();
    }

}