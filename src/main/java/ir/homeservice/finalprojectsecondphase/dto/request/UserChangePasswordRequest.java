package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserChangePasswordRequest(

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "YOUR PASSWORD MUST CONTAIN AT LEAST 1 LETTER, 1 NUMBER, AND BE 8 CHARACTERS LONG")
        @NotBlank(message = "PASSWORD IS REQUIRED")
        String newPassword,

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "YOUR PASSWORD MUST CONTAIN AT LEAST 1 LETTER, 1 NUMBER, AND BE 8 CHARACTERS LONG")
        @NotBlank(message = "CONFIRM PASSWORD IS REQUIRED")
        String confirmNewPassword
) {
}
