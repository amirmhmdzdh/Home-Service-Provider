package ir.homeservice.finalprojectsecondphase.controller;

import ir.homeservice.finalprojectsecondphase.dto.request.*;
import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.dto.response.MainServiceResponse;
import ir.homeservice.finalprojectsecondphase.dto.response.SubServiceResponse;
import ir.homeservice.finalprojectsecondphase.dto.response.UserResponseToLogin;
import ir.homeservice.finalprojectsecondphase.mapper.AdminLoginMapper;
import ir.homeservice.finalprojectsecondphase.mapper.MainServiceMapper;
import ir.homeservice.finalprojectsecondphase.mapper.SubServiceMapper;
import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Admin;
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

        AdminLoginMapper.INSTANCE.signInAdminRequestToModel(request);

        Admin signInAdmin = adminService.signInAdmin(request.email(), request.password());

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
    public ResponseEntity<SubServiceResponse> addSubServices(@RequestBody SubServiceRequest request) {

        SubService requestToModel = SubServiceMapper.INSTANCE.subServiceSaveRequestToModel(request);

        SubService subService = adminService.createSubService(requestToModel);

        SubServiceResponse subServiceResponse = modelMapper.map(subService, SubServiceResponse.class);

        return new ResponseEntity<>(subServiceResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update-SubService")
    public ResponseEntity<SubServiceResponse> updateSubServices(@RequestBody UpdateSubServiceRequest request) {

        SubService requestToModel = SubServiceMapper.INSTANCE.UpdateSubServiceRequestToModel(request);

        SubService subService = adminService.updateSubService(requestToModel);

        SubServiceResponse subServiceResponse = modelMapper.map(subService, SubServiceResponse.class);

        return new ResponseEntity<>(subServiceResponse, HttpStatus.OK);

    }

    @PutMapping("/confirm-Specialist/{specialistId}")
    public ResponseEntity<String> confirmSpecialist(@PathVariable Long specialistId) {
        adminService.confirmSpecialist(specialistId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/add-Specialist-To-SubService/{subServiceId}/{specialistId}")
    public ResponseEntity<String> addSpecialistToSubService(@PathVariable Long subServiceId, @PathVariable Long specialistId) {
        adminService.addSpecialistToSubService(subServiceId, specialistId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-SubServices-From-Specialist/{subServiceId}/{specialistId}")
    public ResponseEntity<String> deleteSubServicesFromSpecialist(@PathVariable Long subServiceId, @PathVariable Long specialistId) {
        adminService.deleteSubServicesFromSpecialist(subServiceId, specialistId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/search")
    public List<FilterUserResponse> search(@RequestBody SearchForUser search) {
        return adminService.searchUser(search);
    }
}