package ir.homeservice.finalprojectsecondphase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CustomerResponseRegister {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Long credit;

    private Long addressId;

    private String addressProvince;

    private String addressCity;

    private String addressAvenue;

    private String addressHouseNumber;

}
