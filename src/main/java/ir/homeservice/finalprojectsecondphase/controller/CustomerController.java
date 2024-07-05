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
import ir.homeservice.finalprojectsecondphase.service.CustomerService;
import ir.homeservice.finalprojectsecondphase.utill.Validation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@RestController
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

    @GetMapping("/signIn-Customer")
    public ResponseEntity<CustomerResponseRegister> signIn(@RequestBody CustomerRequestSignIn request) {

        Customer toModel = CustomerMapper.INSTANCE.signInCustomerToModel(request);

        Customer customer = customerService.signInCustomer(toModel);

        CustomerResponseRegister customerResponseRegister = modelMapper.map(customer, CustomerResponseRegister.class);

        return new ResponseEntity<>(customerResponseRegister, HttpStatus.FOUND);
    }

    @PutMapping("/change-Password-Customer")
    public ResponseEntity<UserChangePasswordResponse> changePassword
            (@Valid @RequestBody UserChangePasswordRequest request) {

        CustomerMapper.INSTANCE.requestDtoToModelToChangePassword(request);

        Customer customer = customerService.changePassword(
                request.email(), request.oldPassword(), request.newPassword());

        UserChangePasswordResponse userChangePasswordResponse = modelMapper.map(
                customer, UserChangePasswordResponse.class);

        return new ResponseEntity<>(userChangePasswordResponse, HttpStatus.OK);
    }

    @PostMapping("/add-Order/{customerId}")
    public ResponseEntity<OrderResponse> addOrders
            (@PathVariable Long customerId, @Valid @RequestBody OrdersRequest request) {

        OrderMapper.INSTANCE.requestDtoToModelToAddOrder(request);

        Orders orders = customerService.watchAndOrder(customerId, request);

        OrderResponse map = modelMapper.map(orders, OrderResponse.class);

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PutMapping("/choose-Offer-for-Order/{offerId}/{customerId}")
    public ResponseEntity<OfferResponse> chooseOfferForOrder(@PathVariable Long offerId, @PathVariable Long customerId) {

        Orders orders = customerService.trackOrders(offerId, customerId);

        OfferResponse map = modelMapper.map(orders, OfferResponse.class);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/show-all-offer-by-price/{orderId}/{customerId}")
    public List<OfferResponse> showAllOfferForOrderByProposedPrice
            (@PathVariable Long orderId, @PathVariable Long customerId) {
        List<Offer> offerListByProposedPrice = customerService.findOfferListByProposedPrice(orderId, customerId);
        OfferResponseMapper mapper = new OfferResponseMapper();
        return offerListByProposedPrice.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/show-all-offer-by-star/{orderId}/{customerId}")
    public List<OfferResponse> showAllOfferForOrderByStar(@PathVariable Long orderId, @PathVariable Long customerId) {
        List<Offer> offerListBySpecialistScore = customerService.findOfferListBySpecialistScore(orderId, customerId);
        OfferResponseMapper mapper = new OfferResponseMapper();
        return offerListBySpecialistScore.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @PutMapping("/order-started/{orderId}/{customerId}")
    public ResponseEntity<OrderResponse> orderStarted(@PathVariable Long orderId, @PathVariable Long customerId) {

        Orders orders = customerService.changeOrderStatusToStarted(orderId, customerId);

        OrderResponse map = modelMapper.map(orders, OrderResponse.class);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/order-done/{orderId}/{customerId}")
    public ResponseEntity<OrderResponse> orderDone(@PathVariable Long orderId, @PathVariable Long customerId) {

        Orders orders = customerService.changeOrderStatusToDone(orderId, customerId);

        OrderResponse map = modelMapper.map(orders, OrderResponse.class);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/increase-account-balance/{customerId}/{credit}")
    public ResponseEntity<CustomerResponseRegister> increaseAccountBalance
            (@PathVariable Long customerId, @PathVariable Long credit) {

        Customer customer = customerService.increaseCustomerCredit(customerId, credit);

        CustomerResponseRegister map = modelMapper.map(customer, CustomerResponseRegister.class);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/paid-in-app-credit/{orderId}/{customerId}")
    public ResponseEntity<CustomerResponseRegister> payByInApp
            (@PathVariable Long orderId, @PathVariable Long customerId) {

        Customer customer = customerService.paidByInAppCredit(orderId, customerId);

        CustomerResponseRegister map = modelMapper.map(customer, CustomerResponseRegister.class);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/pay-online-payment/{orderId}/{customerId}")
    public ModelAndView payByOnlinePayment(@PathVariable Long orderId, @PathVariable Long customerId, Model model) {
        return customerService.payByOnlinePayment(orderId, customerId, model);
    }

    @PostMapping("/send-payment-info")
    public ResponseEntity<CustomerResponseRegister> paymentInfo(@ModelAttribute @Validated PaymentRequest request) {
        validation.checkPaymentRequest(request);

        Customer customer = customerService.changeOrderStatusToPaidByOnlinePayment(request.getCustomerIdOrderId());

        CustomerResponseRegister map = modelMapper.map(customer, CustomerResponseRegister.class);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/register-Comment/{orderId}/{customerId}")
    public ResponseEntity<CommentResponse> registerComment
            (@Valid @RequestBody CommentRequest request, @PathVariable Long orderId, @PathVariable Long customerId) {

        CommentMapper.INSTANCE.registerCommentToModel(request);

        Comment comment = customerService.registerComment(request, orderId, customerId);

        CommentResponse map = modelMapper.map(comment, CommentResponse.class);

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }


}






