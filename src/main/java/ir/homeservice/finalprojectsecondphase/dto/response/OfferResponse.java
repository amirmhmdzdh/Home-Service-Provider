package ir.homeservice.finalprojectsecondphase.dto.response;

import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {

    private Long id;
    private LocalDateTime sendTime;
    private Long ordersId;
    private Long proposedPrice;
    private LocalDateTime executionTime;
    private LocalDateTime endTime;
    private OfferStatus offerStatus;

}
