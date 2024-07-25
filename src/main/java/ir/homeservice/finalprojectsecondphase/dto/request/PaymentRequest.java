package ir.homeservice.finalprojectsecondphase.dto.request;

import ir.homeservice.finalprojectsecondphase.dto.CustomerIdOrderId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {

    @NotNull
    String captcha;
    @NotNull
    String hidden;
    @NotNull
    @Pattern(regexp = "[0-9]{13}(?:[0-9]{3})?$", message = "card number must be 16 digit")
    String number;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z]*$", message = "please check your name . you can't enter space or digit")
    String name;
    @Pattern(regexp = "\\b\\d{3}\\b", message = "your cvc must be 3 digit")
    String cvc;
    @NotNull
    int year;
    @NotNull
    int month;
    @Valid
    CustomerIdOrderId customerIdOrderId;
}
