package ir.homeservice.finalprojectsecondphase.repository;

import ir.homeservice.finalprojectsecondphase.dto.response.OfferResponse;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query(" from Offer o where o.orders.id = :orderId and o.offerStatus= :offerStatus order by o.proposedPrice asc")
    List<Offer> findOfferListByProposedPrice(Long orderId, OfferStatus offerStatus);

    @Query(" from Offer o where o.orders.id = :orderId and o.offerStatus= :offerStatus order by o.specialist.star desc")
    List<Offer> findOfferListBySpecialistScore(Long orderId, OfferStatus offerStatus);
}
