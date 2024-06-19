package ir.homeservice.finalprojectsecondphase.utill;


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
            throw new AlphabetException("the wording of the text is not incorrect!");
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

    public byte[] checkImage(byte[] imagePath) {
        if (imagePath == null) {
            throw new ImageFormatException("The image is empty!");
        }
        String imageString = new String(imagePath);
        if (imageString.matches(".*\\.(jpg|jpeg)$")) {
            throw new ImageFormatException("Invalid file format. Only JPG and JPEG formats are supported.");
        }
        if (imagePath != null && imagePath.length > 300 * 1024) {
            throw new ImageFormatException("The size of the image exceeds the limit!");
        }
        return imagePath;
    }

    public boolean checkOfferBelongToTheOrder(Long offerId, Customer customer) {
        boolean[] exist = new boolean[1];
        customer.getOrdersList().forEach(order -> exist[0] = order.getOfferList().stream().filter(offer ->
                offer.getId().equals(offerId)).findFirst().isEmpty());
        if (exist[0])
            throw new OfferNotExistException("this offer not belong to your orders");
        return true;
    }

    public boolean checkOwnerOfTheOrder(Long orderId, Customer customer) {
        if (customer.getOrdersList().stream().filter(o -> o.getId().equals(orderId)).findFirst().isEmpty())
            throw new OrderIsNotExistException("you are not the owner of this order");
        return true;
    }

    public void validatePrice(SubService subService, Orders order) {
        if (subService.getBasePrice() > order.getProposedPrice()) {
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


}