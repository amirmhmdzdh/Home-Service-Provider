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
    private Integer requestOfOrders;
    private Integer doneOrders_JustForSpecialist;
    private String userType;
    private String userStatus_JustForSpecialist;
    private LocalDateTime userCreationDate;
    private Double star_JustForSpecialist;
}