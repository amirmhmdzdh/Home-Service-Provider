package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;

    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    public Optional<Offer> findById(Long id) {
        return offerRepository.findById(id);
    }

    public List<Offer> findOfferListByProposedPrice(Long orderId) {
        return offerRepository.findOfferListByProposedPrice(orderId, OfferStatus.WAITING);
    }

    public List<Offer> findOfferListBySpecialistScore(Long orderId) {
        return offerRepository.findOfferListBySpecialistScore(orderId, OfferStatus.WAITING);
    }

    public List<Offer> findOffersBySpecialistIdAndOfferStatus(Long workerId, OfferStatus offerStatus) {
        return offerRepository.findOffersBySpecialistIdAndOfferStatus(workerId, offerStatus);
    }

}
