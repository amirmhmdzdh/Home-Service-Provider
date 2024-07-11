package ir.homeservice.finalprojectsecondphase.dto.request;

public record AdminRegisterRequest
        (
                String firstName,
                String lastName,
                String email,
                String password
        ) {


}
