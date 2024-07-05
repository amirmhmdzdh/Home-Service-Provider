package ir.homeservice.finalprojectsecondphase.controller;

import ir.homeservice.finalprojectsecondphase.dto.request.OfferRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.SpecialistRegisterRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.SpecialistSignInRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.UserChangePasswordRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.OfferResponse;
import ir.homeservice.finalprojectsecondphase.dto.response.SpecialistResponseRegister;
import ir.homeservice.finalprojectsecondphase.dto.response.UserChangePasswordResponse;
import ir.homeservice.finalprojectsecondphase.mapper.OfferMapper;
import ir.homeservice.finalprojectsecondphase.mapper.SpecialistMapper;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.service.SpecialistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SpecialistController {
    private final SpecialistService specialistService;
    private final ModelMapper modelMapper;

    @PostMapping("/register-Specialist")
    public ResponseEntity<SpecialistResponseRegister> signUp
            (@Valid @RequestBody SpecialistRegisterRequest request, String image) {

        Specialist model = SpecialistMapper.INSTANCE.registerSpecialistToModel(request);

        Specialist specialist = specialistService.signUpSpecialist(model, image);

        SpecialistResponseRegister specialistResponseRegister = modelMapper
                .map(specialist, SpecialistResponseRegister.class);

        return new ResponseEntity<>(specialistResponseRegister, HttpStatus.CREATED);
    }

    @GetMapping("/signIn-Specialist")
    public ResponseEntity<SpecialistResponseRegister> signIn(@RequestBody SpecialistSignInRequest request) {

        Specialist model = SpecialistMapper.INSTANCE.signInSpecialistToModel(request);

        Specialist specialist = specialistService.signInSpecialist(model.getEmail(), model.getPassword());

        SpecialistResponseRegister specialistResponseRegister = modelMapper
                .map(specialist, SpecialistResponseRegister.class);

        return new ResponseEntity<>(specialistResponseRegister, HttpStatus.FOUND);
    }

    @PutMapping("/change-Password-Specialist")
    public ResponseEntity<UserChangePasswordResponse> changePasswordSpecialist
            (@Valid @RequestBody UserChangePasswordRequest request) {

       SpecialistMapper.INSTANCE.requestDtoToModelToChangePassword(request);

        Specialist specialist = specialistService
                .changePasswordSpecialist(request.email(), request.oldPassword(), request.newPassword());

        UserChangePasswordResponse changePasswordResponse = modelMapper.map(specialist, UserChangePasswordResponse.class);

        return new ResponseEntity<>(changePasswordResponse, HttpStatus.OK);
    }

    @PostMapping("/add-Offer-for-order/{specialistId}")
    public ResponseEntity<OfferResponse> addOfferForOrder
            (@Valid @RequestBody OfferRequest request, @PathVariable Long specialistId) {

        OfferMapper.INSTANCE.offerSaveRequestToModel(request);

        Offer offer = specialistService.newOffers(request, specialistId);

        OfferResponse map = modelMapper.map(offer, OfferResponse.class);

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
    @GetMapping("/show-specialist-star/{specialistId}")
    public Double viewWorkerCredit(@PathVariable Long specialistId) {
        return specialistService.getSpecialistRate(specialistId);
    }

}
