package ir.homeservice.finalprojectsecondphase.model.offer;

import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
public class Offer extends BaseEntity<Long> {

    private LocalDateTime executionTime;

    private LocalDateTime endTime;

    private LocalDateTime sendTime;


    private Long proposedPrice;


    @Enumerated(value = EnumType.STRING)
    private OfferStatus offerStatus;


    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Orders orders;


    @ManyToOne(fetch = FetchType.EAGER)
    private Specialist specialist;
}
