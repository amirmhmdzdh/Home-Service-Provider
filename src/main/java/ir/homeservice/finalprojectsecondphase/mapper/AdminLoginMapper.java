package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.UserRequestToLogin;
import ir.homeservice.finalprojectsecondphase.dto.response.UserResponseToLogin;
import ir.homeservice.finalprojectsecondphase.model.user.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminLoginMapper {

    AdminLoginMapper INSTANCE = Mappers.getMapper(AdminLoginMapper.class);

    Admin signInAdminRequestToModel(UserRequestToLogin request);

    UserResponseToLogin modelToLogInAdminResponse(Admin admin);
}

