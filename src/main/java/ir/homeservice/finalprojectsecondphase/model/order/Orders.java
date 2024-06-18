package ir.homeservice.finalprojectsecondphase.model.order;

import ir.homeservice.finalprojectsecondphase.model.address.Address;
import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import ir.homeservice.finalprojectsecondphase.model.comment.Comment;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Order_Table")
@SuperBuilder
public class Orders extends BaseEntity<Long> {

    private LocalDateTime executionTime;


    private LocalDateTime endTime;


    @NotNull(message = "Filling it is mandatory")
    private Long proposedPrice;


    @Pattern(regexp = "^[a-zA-Z ]{5,}$", message = "Invalid Description!")
    private String description;


    @Enumerated(value = STRING)
    private OrderStatus orderStatus;


    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;


    @ManyToOne(fetch = FetchType.EAGER)
    private SubService subServices;


    @OneToMany(mappedBy = "orders", fetch = FetchType.EAGER
            , cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private List<Offer> offerList;


    @OneToOne
    private Comment comment;


    @OneToOne
    private Address address;
}
