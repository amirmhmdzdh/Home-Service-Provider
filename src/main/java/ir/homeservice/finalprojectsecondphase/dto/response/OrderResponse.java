package ir.homeservice.finalprojectsecondphase.dto.response;

import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OrderResponse {
    private Long id;
    private LocalDateTime executionTime;
    private LocalDateTime endTime;
    private Long proposedPrice;
    private String description;
    private OrderStatus orderStatus;


}
