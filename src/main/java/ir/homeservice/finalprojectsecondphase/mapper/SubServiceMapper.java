package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.SubServiceRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.UpdateSubServiceRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.SubServiceResponse;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubServiceMapper {

    SubServiceMapper INSTANCE = Mappers.getMapper(SubServiceMapper.class);
    SubService subServiceSaveRequestToModel(SubServiceRequest request);

    SubServiceResponse modelToSubServiceSaveResponse(SubService service);

    @Mapping(source="id", target="id")
    SubService UpdateSubServiceRequestToModel(UpdateSubServiceRequest request);
}
