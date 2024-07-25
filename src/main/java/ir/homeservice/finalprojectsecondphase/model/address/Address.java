package ir.homeservice.finalprojectsecondphase.model.address;

import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@SuperBuilder
public class Address extends BaseEntity<Long> {

    private String province;


    private String city;


    private String avenue;


    private String houseNumber;


    @OneToOne
    private Customer customer;
}
