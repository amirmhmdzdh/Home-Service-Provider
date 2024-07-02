package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.response.OfferResponse;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import org.modelmapper.ModelMapper;

public class OfferResponseMapper {
    public OfferResponse map(Offer offer) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(offer, OfferResponse.class);
    }
}