package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record SubServiceRequest(
        @Pattern(regexp = "^[a-zA-Z ]{3,}$", message = "please enter valid name.")
        String name,
        @Pattern(regexp = "^[a-zA-Z ]{3,}$", message = "please enter valid name.")
        String description,
        @Min(value = 10, message = "cant less than 10$")
        Long basePrice,
        MainServiceRequest mainService) {
}
