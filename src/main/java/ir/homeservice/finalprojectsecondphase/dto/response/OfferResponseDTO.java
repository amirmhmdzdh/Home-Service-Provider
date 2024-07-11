package ir.homeservice.finalprojectsecondphase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
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
public class OfferResponseDTO {

    Long offerId;
    Long specialistId;
    String specialistEmail;
    Long offerProposedPrice;
    OfferStatus offerStatus;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime executionTime;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime endTime;
}