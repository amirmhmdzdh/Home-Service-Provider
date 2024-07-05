package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.SpecialistRegisterRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.SpecialistSignInRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.UserChangePasswordRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.SpecialistResponseRegister;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpecialistMapper {

    SpecialistMapper INSTANCE = Mappers.getMapper(SpecialistMapper.class);

    Specialist registerSpecialistToModel(SpecialistRegisterRequest request);

    SpecialistResponseRegister modelToRegister(Specialist specialist);

    Specialist signInSpecialistToModel(SpecialistSignInRequest request);

    Specialist requestDtoToModelToChangePassword(UserChangePasswordRequest request);

}
