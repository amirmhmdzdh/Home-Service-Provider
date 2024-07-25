package ir.homeservice.finalprojectsecondphase.model.service;

import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubService extends BaseEntity<Long> {

    @Column(unique = true)
    private String name;

    private Long basePrice;


    private String description;

    @ManyToOne
    private MainService mainService;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private List<Specialist> specialistList;


    @OneToMany(mappedBy = "subServices",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Orders> ordersList;
}
