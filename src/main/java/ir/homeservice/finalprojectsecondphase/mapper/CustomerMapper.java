package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.CustomerRequest;
import ir.homeservice.finalprojectsecondphase.dto.request.CustomerRequestSignIn;
import ir.homeservice.finalprojectsecondphase.dto.request.UserChangePasswordRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.CustomerResponseRegister;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer registerCustomerToModel(CustomerRequest request);

    CustomerResponseRegister modelToRegister(Customer customer);


    Customer signInCustomerToModel(CustomerRequestSignIn requestSignIn);

    Customer requestDtoToModelToChangePassword(UserChangePasswordRequest request);
}
