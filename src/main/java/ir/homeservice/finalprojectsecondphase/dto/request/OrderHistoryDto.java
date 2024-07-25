package ir.homeservice.finalprojectsecondphase.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class OrderHistoryDto {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endDate;

    OrderStatus status;

    String mainServiceName;

    String subServiceName;
}