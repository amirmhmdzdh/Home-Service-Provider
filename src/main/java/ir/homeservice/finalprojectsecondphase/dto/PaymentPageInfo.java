package ir.homeservice.finalprojectsecondphase.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentPageInfo {


    Long price;
    String captcha;
    String hidden;
    String image;
    CustomerIdOrderId customerIdOrderId;

}
