package ir.homeservice.finalprojectsecondphase.model.address;

import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@SuperBuilder
public class Address extends BaseEntity<Long> {

   // @Pattern(regexp = "^[a-zA-Z ]{3,}$", message = "Invalid Province!")
    private String province;


   // @Pattern(regexp = "^[a-zA-Z ]{3,}$", message = "Invalid!")
    private String city;


   // @Pattern(regexp = "^[a-zA-Z ]{3,}$", message = "Invalid Avenue")
    private String avenue;


   // @Pattern(regexp = "^\\d{1,4}$", message = "Invalid House Number")
    private String houseNumber;


    @OneToOne
    private Customer customer;


    public Address(String province, String city, String avenue, String houseNumber, Customer customer) {
        this.province = province;
        this.city = city;
        this.avenue = avenue;
        this.houseNumber = houseNumber;
        this.customer = customer;
    }
}
