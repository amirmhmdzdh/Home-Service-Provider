package ir.homeservice.finalprojectsecondphase.model.comment;

import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class Comment extends BaseEntity<Long> {

    @NotNull(message = "Filling it is mandatory")
    private Integer star;

    private String textComment;

    @OneToOne
    private Orders orders;

}

