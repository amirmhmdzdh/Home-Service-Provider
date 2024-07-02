package ir.homeservice.finalprojectsecondphase.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SubServiceResponse {
    private Long id;
    private String name;
    private String description;
    private Long basePrice;
    private String mainServiceName;

}