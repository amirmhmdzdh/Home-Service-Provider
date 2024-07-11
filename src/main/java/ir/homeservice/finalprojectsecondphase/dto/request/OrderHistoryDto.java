package ir.homeservice.finalprojectsecondphase.dto.request;

import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Getter
public class OrderHistoryDto {

    LocalDateTime startDate;

    LocalDateTime endDate;

    OrderStatus status;

    String mainServiceName;

    String subServiceName;
}