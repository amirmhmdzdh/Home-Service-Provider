package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMappers {

    public static FilterUserResponse convertToFilterDTO(Customer customer) {
        return new FilterUserResponse(

                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCredit(),
                customer.getRole().name(),
                customer.getRegistrationTime()
        );
    }
}
