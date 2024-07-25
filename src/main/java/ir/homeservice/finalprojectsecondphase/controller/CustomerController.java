package ir.homeservice.finalprojectsecondphase.controller;

import ir.homeservice.finalprojectsecondphase.dto.request.*;
import ir.homeservice.finalprojectsecondphase.dto.response.*;
import ir.homeservice.finalprojectsecondphase.mapper.CommentMapper;
import ir.homeservice.finalprojectsecondphase.mapper.CustomerMapper;
import ir.homeservice.finalprojectsecondphase.mapper.OfferResponseMapper;
import ir.homeservice.finalprojectsecondphase.mapper.OrderMapper;
import ir.homeservice.finalprojectsecondphase.model.comment.Comment;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import ir.homeservice.finalprojectsecondphase.model.user.Users;
import ir.homeservice.finalprojectsecondphase.service.CustomerService;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final Validation validation;
    private final ModelMapper modelMapper;
    private final CustomerService customerService;

    @PostMapping("/register-Customer")
    public ResponseEntity<CustomerResponseRegister> signUp(@Valid @RequestBody CustomerRequest request) {
        CustomerMapper.INSTANCE.registerCustomerToModel(request);
        Customer customer = customerService.signUpCustomer(request);
        CustomerResponseRegister customerResponseRegister = modelMapper.map(customer, CustomerResponseRegister.class);
        return new ResponseEntity<>(customerResponseRegister, HttpStatus.CREATED);
    }

    @PutMapping("/change-Password-Customer")
    public ResponseEntity<String> changePassword
            (@Valid @RequestBody UserChangePasswordRequest request, Authentication authentication) {
        customerService.changePassword(request, ((Users) authentication.getPrincipal()).getId());
        return new ResponseEntity<>("PASSWORD CHANGED SUCCESSFULLY", HttpStatus.OK);
    }

    @PostMapping("/add-Order")
    public ResponseEntity<OrderResponse> addOrders
            (Authentication authentication, @Valid @RequestBody OrdersRequest request) {
        OrderMapper.INSTANCE.requestDtoToModelToAddOrder(request);
        Orders orders = customerService.watchAndOrder(((Users) authentication.getPrincipal()).getId(), request);
        OrderResponse map = modelMapper.map(orders, OrderResponse.class);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PutMapping("/choose-Offer-for-Order/{offerId}")
    public ResponseEntity<OfferResponse> chooseOfferForOrder(@PathVariable Long offerId, Authentication authentication) {
        Orders orders = customerService.trackOrders(offerId, ((Customer) authentication.getPrincipal()));
        OfferResponse map = modelMapper.map(orders, OfferResponse.class);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/show-all-offer-by-price/{orderId}")
    public List<OfferResponse> showAllOfferForOrderByProposedPrice
            (@PathVariable Long orderId, Authentication authentication) {
        List<Offer> offerListByProposedPrice = customerService
                .findOfferListByProposedPrice(orderId, ((Customer) authentication.getPrincipal()));
        OfferResponseMapper mapper = new OfferResponseMapper();
        return offerListByProposedPrice.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/show-all-offer-by-star/{orderId}")
    public List<OfferResponse> showAllOfferForOrderByStar(@PathVariable Long orderId, Authentication authentication) {
        List<Offer> offerListBySpecialistScore = customerService
                .findOfferListBySpecialistScore(orderId, ((Customer) authentication.getPrincipal()));
        OfferResponseMapper mapper = new OfferResponseMapper();
        return offerListBySpecialistScore.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @PutMapping("/order-started/{orderId}")
    public ResponseEntity<OrderResponse> orderStarted(@PathVariable Long orderId, Authentication authentication) {
        Orders orders = customerService.changeOrderStatusToStarted(orderId, ((Users) authentication.getPrincipal()));
        OrderResponse map = modelMapper.map(orders, OrderResponse.class);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/order-done/{orderId}")
    public ResponseEntity<OrderResponse> orderDone(@PathVariable Long orderId, Authentication authentication) {
        Orders orders = customerService.changeOrderStatusToDone(orderId, ((Customer) authentication.getPrincipal()));
        OrderResponse map = modelMapper.map(orders, OrderResponse.class);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/increase-account-balance/{credit}")
    public ResponseEntity<CustomerResponseRegister> increaseAccountBalance
            (Authentication authentication, @PathVariable Long credit) {
        Customer customer = customerService.increaseCustomerCredit(((Customer) authentication.getPrincipal()), credit);
        CustomerResponseRegister map = modelMapper.map(customer, CustomerResponseRegister.class);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/paid-in-app-credit/{orderId}")
    public ResponseEntity<CustomerResponseRegister> payByInApp
            (@PathVariable Long orderId, Authentication authentication) {
        Customer customer = customerService.paidByInAppCredit(orderId, ((Customer) authentication.getPrincipal()));
        CustomerResponseRegister map = modelMapper.map(customer, CustomerResponseRegister.class);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/pay-online-payment/{orderId}")
    public ModelAndView payByOnlinePayment(@PathVariable Long orderId, Model model, Authentication authentication) {
        return customerService.payByOnlinePayment(orderId, ((Customer) authentication.getPrincipal()), model);
    }

    @PostMapping("/send-payment-info")
    public ResponseEntity<String> paymentInfo(@ModelAttribute @Validated PaymentRequest request) {
        validation.checkPaymentRequest(request);
        customerService.getInfoPaidByOnlinePayment(request.getCustomerIdOrderId());
        return new ResponseEntity<>("Thank you for your payment!", HttpStatus.OK);
    }

    @PostMapping("/register-Comment/{orderId}")
    public ResponseEntity<CommentResponse> registerComment
            (@Valid @RequestBody CommentRequest request, @PathVariable Long orderId, Authentication authentication) {
        CommentMapper.INSTANCE.registerCommentToModel(request);
        Comment comment = customerService.registerComment(request, orderId, ((Customer) authentication.getPrincipal()));
        CommentResponse map = modelMapper.map(comment, CommentResponse.class);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping("/history-of-order/{orderStatus}")
    public List<FilterOrderResponseDTO> filterOrder(
            @PathVariable String orderStatus, Authentication authentication) {
        List<Orders> orders = customerService.filterOrder(orderStatus,
                ((Users) authentication.getPrincipal()).getId());
        List<FilterOrderResponseDTO> orderResponseDTOS = new ArrayList<>();
        for (Orders orders1 : orders) {
            FilterOrderResponseDTO map = modelMapper.map(orders1, FilterOrderResponseDTO.class);
            orderResponseDTOS.add(map);
        }
        return orderResponseDTOS;
    }

    @GetMapping("/show-my-credit")
    public Long showCustomerCredit(Authentication authentication) {
        return customerService.getCustomerCredit(
                ((Users) authentication.getPrincipal()).getId());
    }
}






