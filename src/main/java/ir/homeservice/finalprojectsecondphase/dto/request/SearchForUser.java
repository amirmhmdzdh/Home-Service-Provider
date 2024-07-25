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
    private String subServiceName;
    private String orderStatus;
    private Integer minRequestOfOrders;
    private Integer maxRequestOfOrders;
    private Integer minDoneOrders;
    private Integer maxDoneOrders;
    private Double minScore;
    private Double maxScore;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime minUserCreationAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime maxUserCreationAt;
}
