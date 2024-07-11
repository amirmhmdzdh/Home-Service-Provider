package ir.homeservice.finalprojectsecondphase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterOrderResponseDTO {

    String description;
    Long proposedPrice;
    OrderStatus orderStatus;
    String subServicesName;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime registrationTime;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime endTime;
    Long acceptedOfferId;
}