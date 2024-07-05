package ir.homeservice.finalprojectsecondphase.dto.response;

import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SpecialistResponseRegister {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private SpecialistStatus status;
}
