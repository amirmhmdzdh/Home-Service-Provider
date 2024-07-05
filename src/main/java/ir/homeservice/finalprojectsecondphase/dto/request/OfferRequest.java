package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record OfferRequest(
        Long orderId,
        @Min(10)
        Long offerProposedPrice,

        LocalDateTime proposedStartDate,

        LocalDateTime proposedEndDate
) {
}
