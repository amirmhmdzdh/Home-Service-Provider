package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMappers {

    public static FilterUserResponse convertToFilterDTO(Customer customer) {
        return FilterUserResponse.builder()
                .userId(customer.getId())
                .firstname(customer.getFirstName())
                .lastname(customer.getLastName())
                .email(customer.getEmail())
                .credit(customer.getCredit())
                .requestOfOrders(customer.getRequestOfOrders())
                .userType(customer.getRole().name())
                .userCreationDate(customer.getRegistrationTime())
                .build();
    }
}

