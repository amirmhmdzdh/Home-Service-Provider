package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Pattern;

public record MainServiceRequest(
      @Pattern(regexp = "^[a-zA-Z ]{3,}$",message = "please enter valid name.")
        String name
) {
}
