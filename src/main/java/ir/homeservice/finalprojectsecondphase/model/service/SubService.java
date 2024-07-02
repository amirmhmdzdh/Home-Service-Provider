package ir.homeservice.finalprojectsecondphase.model.service;

import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

   // @Pattern(regexp = "^[a-zA-Z ]{3,}$", message = "Invalid name!")
    @Column(unique = true)
    private String name;

   // @NotNull(message = "Filling it is mandatory")
    private Long basePrice;


   // @Pattern(regexp = "^[a-zA-Z ]{5,}$", message = "Invalid Description!")
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

    public SubService(String name, Long basePrice, String description, MainService mainService) {
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.mainService = mainService;
    }
}
