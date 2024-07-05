package ir.homeservice.finalprojectsecondphase.utill;


import ir.homeservice.finalprojectsecondphase.dto.request.OrdersRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.PaymentRequest;
import ir.homeservice.finalprojectsecondphase.exception.*;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Component
public class Validation {

    public boolean checkBlank(String str) {
        if (str.isBlank()) throw new TextBlankException("the string input is blank!");
        return true;
    }

    public boolean checkText(String text) {
        String regex = "^[a-zA-Z ]{3,}$";
        if (!Pattern.matches(regex, text))
            throw new AlphabetException("the word is not incorrect!");
        return true;
    }

    public boolean checkNumber(Long longDigit) {
        if (longDigit <= 0)
            throw new PositiveNumberException("the number is not Valid");
        return true;
    }

    public boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        if (!Pattern.matches(emailRegex, email))
            throw new EmailFormatException("the format of the email is incorrect!");
        return true;
    }

    public boolean checkPassword(String password) {
        String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        if (!Pattern.matches(passwordRegex, password))
            throw new PasswordFormatException("the format of the password is incorrect!");
        return true;
    }

    public byte[] checkImage(String image) {
        if (image == null) {
            throw new ImageFormatException("The image is empty!");
        }

        if (!image.matches(".*\\.(jpg|jpeg)$")) {
            throw new ImageFormatException("Invalid file format. Only JPG and JPEG formats are supported.");
        }
        byte[] imageBytes = image.getBytes();

        if (imageBytes.length > 300 * 1024) {
            throw new ImageFormatException("The size of the image exceeds the limit!");
        }

        return imageBytes;
    }

    public boolean checkOfferBelongToTheOrder(Long offerId, Customer customer) {
        boolean exists = customer.getOrdersList()
                .stream()
                .anyMatch(order -> order.getOfferList()
                        .stream()
                        .anyMatch(offer -> offer.getId().equals(offerId)));
        if (!exists) {
            throw new NotFoundException("This offer does not belong to your orders.");
        }
        return true;
    }

    public boolean checkOwnerOfTheOrder(Long orderId, Customer customer) {
        if (customer.getOrdersList().stream().filter(o -> o.getId().equals(orderId)).findFirst().isEmpty())
            throw new NotFoundException("you are not the owner of this order");
        return true;
    }

    public void validatePrice(SubService subService, OrdersRequest order) {
        if (subService.getBasePrice() > order.proposedPrice()) {
            throw new PriceException("The suggested price can be lower than the base price.");
        }
    }

    public void validateTime(Orders orders) {
        if (orders.getExecutionTime().isBefore(LocalDateTime.now())) {
            throw new TimeException("Execution time before current time");
        }

        if (orders.getEndTime().isBefore(orders.getExecutionTime())) {
            throw new TimeException("End time before execution time");
        }
    }
    public boolean checkPaymentRequest(PaymentRequest dto) {
        if (!dto.getCaptcha().equals(dto.getHidden())) {
            throw new NotFoundException("wrong captcha");
        }
        if (Integer.parseInt(dto.getYear()) < LocalDateTime.now().getYear()) {
            throw new TimeException("expired card ");
        }
        if (Integer.parseInt(dto.getYear()) == LocalDateTime.now().getYear() &&
                Integer.parseInt(dto.getMonth()) < LocalDateTime.now().getMonth().getValue()) {
            throw new TimeException("expired card ");
        }
        return true;
    }

}