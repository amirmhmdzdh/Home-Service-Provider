package ir.homeservice.finalprojectsecondphase.dto.request;
public record CustomerRequest(String firstName,
                              String lastName,
                              String email,
                              String password,
                              AddressRequest request
) {
}
