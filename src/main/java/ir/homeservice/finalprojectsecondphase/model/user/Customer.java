package ir.homeservice.finalprojectsecondphase.model.user;


import ir.homeservice.finalprojectsecondphase.model.address.Address;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
public class Customer extends Users {

    @NotNull(message = "Recharging the wallet is required!")
    private Long credit;

    private Integer requestOfOrders;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Address address;

    @OneToMany(mappedBy = "customer",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Orders> ordersList;
}