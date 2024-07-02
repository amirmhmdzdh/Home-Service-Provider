package ir.homeservice.finalprojectsecondphase.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponseToLogin{

    private Long id;

    private String email;

    private String password;

    private String firstName;

}