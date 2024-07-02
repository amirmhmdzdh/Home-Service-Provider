package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.OfferRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.UserRequestToLogin;
import ir.homeservice.finalprojectsecondphase.dto.response.OfferResponse;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    Offer offerSaveRequestToModel(OfferRequest request);

    @Mapping(source = "orders.id", target = "ordersId")
    OfferResponse modelToOfferSaveResponse(Offer offer);

    Offer trackOrders(UserRequestToLogin request);


}
