package ir.homeservice.finalprojectsecondphase.dto.request;

import java.time.LocalDateTime;

public record OfferRequest(
        Long orderId,
        Long offerProposedPrice,
        LocalDateTime proposedStartDate,
        LocalDateTime proposedEndDate
) {
}
