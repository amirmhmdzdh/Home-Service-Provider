package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import org.springframework.stereotype.Component;

@Component
public class SpecialistMappers {

    public static FilterUserResponse convertToFilterDTO(Specialist specialist) {
        return new FilterUserResponse(
                specialist.getId(),
                specialist.getFirstName(),
                specialist.getLastName(),
                specialist.getEmail(),
                specialist.getCredit(),
                specialist.getRole().name(),
                specialist.getStatus().name(),
                specialist.getIsActive(),
                specialist.getRegistrationTime(),
                specialist.getStar()
        );
    }
}