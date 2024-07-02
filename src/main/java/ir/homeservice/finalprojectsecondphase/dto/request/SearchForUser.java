package ir.homeservice.finalprojectsecondphase.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchForUser {

    private String userType;
    private Boolean isActive;
    private String userStatus;
    private String firstName;
    private String lastName;
    private String email;

    private Double star;
    private Double minScore;
    private Double maxScore;
    private Long minCredit;
    private Long maxCredit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime minUserCreationAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime maxUserCreationAt;

}
