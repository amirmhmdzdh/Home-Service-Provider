package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import org.springframework.stereotype.Component;

@Component
public class SpecialistMappers {

    public static FilterUserResponse convertToFilterDTO(Specialist specialist) {
        return FilterUserResponse.builder()
                .userId(specialist.getId())
                .firstname(specialist.getFirstName())
                .lastname(specialist.getLastName())
                .email(specialist.getEmail())
                .credit(specialist.getCredit())
                .userType(specialist.getRole().name())
                .userStatus(specialist.getStatus().name())
                .userCreationDate(specialist.getRegistrationTime())
                .star_JustForSpecialist(specialist.getStar())
                .build();
    }
}
