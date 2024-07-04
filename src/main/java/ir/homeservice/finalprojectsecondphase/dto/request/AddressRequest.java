package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Pattern;

public record AddressRequest(
        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String province,
        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String city,
        @Pattern(regexp = "^[a-zA-Z ]{3,}$")
        String avenue,
        @Pattern(regexp = "^\\d{1,4}$")
        String houseNumber) {
}
