package ir.homeservice.finalprojectsecondphase.controller;

import ir.homeservice.finalprojectsecondphase.dto.request.OfferRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.SpecialistRegisterRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.UserChangePasswordRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.OfferResponse;
import ir.homeservice.finalprojectsecondphase.dto.response.OfferResponseDTO;
import ir.homeservice.finalprojectsecondphase.dto.response.SpecialistResponseRegister;
import ir.homeservice.finalprojectsecondphase.mapper.OfferMapper;
import ir.homeservice.finalprojectsecondphase.mapper.SpecialistMapper;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.Users;
import ir.homeservice.finalprojectsecondphase.service.SpecialistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/specialist")
public class SpecialistController {
    private final SpecialistService specialistService;
    private final ModelMapper modelMapper;

    @PostMapping("/signUp-Specialist")
    public ResponseEntity<SpecialistResponseRegister> signUp
            (@Valid @RequestBody SpecialistRegisterRequest request, String image) {

        Specialist model = SpecialistMapper.INSTANCE.registerSpecialistToModel(request);

        Specialist specialist = specialistService.signUpSpecialist(model, image);

        SpecialistResponseRegister specialistResponseRegister = modelMapper
                .map(specialist, SpecialistResponseRegister.class);

        return new ResponseEntity<>(specialistResponseRegister, HttpStatus.CREATED);
    }

    @PutMapping("/change-Password-Specialist")
    public ResponseEntity<String> changePasswordSpecialist
            (@Valid @RequestBody UserChangePasswordRequest request, Authentication authentication) {
        specialistService
                .changePasswordSpecialist(request, ((Users) authentication.getPrincipal()).getId());

        return new ResponseEntity<>("PASSWORD CHANGED SUCCESSFULLY", HttpStatus.OK);
    }

    @PostMapping("/add-Offer-for-order")
    public ResponseEntity<OfferResponse> addOfferForOrder
            (@Valid @RequestBody OfferRequest request, Authentication authentication) {

        OfferMapper.INSTANCE.offerSaveRequestToModel(request);

        Offer offer = specialistService.newOffers(request, ((Users) authentication.getPrincipal()).getId());

        OfferResponse map = modelMapper.map(offer, OfferResponse.class);

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping("/show-specialist-star")
    public Double viewWorkerRate(Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        return specialistService.getSpecialistRate(specialist.getId());
    }

    @GetMapping("/show-specialist-credit")
    public Long viewWorkerCredit(Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        return specialistService.getSpecialistCredit(specialist.getId());
    }

    @GetMapping("/find-all-orders-by-specialist")
    public List<Orders> findAllOrdersBySpecialist
            (@RequestParam(required = false) OrderStatus status, Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        return specialistService.findAllOrdersBySpecialist(status, specialist);
    }

    @GetMapping("/show-all-offers-accepted")
    public List<OfferResponseDTO> viewAllAcceptedOffers
            (@RequestParam(required = false) OfferStatus status, Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();

        List<Offer> offers = specialistService.showAllOffersAccepted(status, specialist);

        List<OfferResponseDTO> offerResponse = new ArrayList<>();

        for (Offer offer : offers) {

            OfferResponseDTO map = modelMapper.map(offer, OfferResponseDTO.class);

            offerResponse.add(map);
        }
        return offerResponse;
    }
}
