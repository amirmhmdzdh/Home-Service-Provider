package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record OrdersRequest(
        Long subServiceId,

        @Min(10)
        Long proposedPrice,

        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String description,

        LocalDateTime workStartDate,

        LocalDateTime workEndDate) {
}
