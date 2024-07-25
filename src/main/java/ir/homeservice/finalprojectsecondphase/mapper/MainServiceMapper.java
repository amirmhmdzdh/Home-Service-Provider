package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.MainServiceRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.MainServiceResponse;
import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MainServiceMapper {

    MainServiceMapper INSTANCE = Mappers.getMapper(MainServiceMapper.class);

    MainService mainServiceSaveRequestToModel(MainServiceRequest request);

    MainServiceResponse modelToMainServiceSaveResponse(MainService mainService);
}
