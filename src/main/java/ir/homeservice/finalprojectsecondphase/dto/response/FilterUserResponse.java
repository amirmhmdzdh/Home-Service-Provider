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

    Long userId;
    String firstname;
    String lastname;
    String email;
    Long credit;
    String userType;
    String userStatus;
    Boolean isActive;
    LocalDateTime userCreationDate;
    Double star_JustForSpecialist;

    public FilterUserResponse(Long id, String firstName, String lastName, String email, Long credit, String userType, LocalDateTime registrationTime) {
        this.userId = id;
        this.firstname = firstName;
        this.lastname = lastName;
        this.email = email;
        this.credit = credit;
        this.userType = userType;
        this.isActive = true;
        this.userCreationDate = registrationTime;

    }
}