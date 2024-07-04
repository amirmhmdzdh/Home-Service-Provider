package ir.homeservice.finalprojectsecondphase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterUserResponse {

    private Long userId;
    private String firstname;
    private String lastname;
    private String email;
    private Long credit;
    private String userType;
    private String userStatus;
    private LocalDateTime userCreationDate;
    private Double star_JustForSpecialist;
}