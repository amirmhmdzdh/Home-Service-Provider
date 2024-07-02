package ir.homeservice.finalprojectsecondphase.dto.request;

public record UserChangePasswordRequest(String email,
                                        String oldPassword,
                                        String newPassword) {
}
