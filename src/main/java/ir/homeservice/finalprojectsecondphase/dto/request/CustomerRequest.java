package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(
        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String firstName,
        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String lastName,
        @Email
        String email,
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        String password,

        AddressRequest request
) {
}
