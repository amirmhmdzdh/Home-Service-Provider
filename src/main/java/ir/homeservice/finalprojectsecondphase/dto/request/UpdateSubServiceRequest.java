package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record UpdateSubServiceRequest(

        Long id,
        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String name,
        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String description,
        @Min(10)
        Long basePrice) {
}
