package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Pattern;

public record UserChangePasswordRequest(

        String email,

        String oldPassword,

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        String newPassword) {
}
