package ir.homeservice.finalprojectsecondphase.model.service;


import ir.homeservice.finalprojectsecondphase.model.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class MainService extends BaseEntity<Long> {

    @Pattern(regexp = "^[a-zA-Z ]{3,}$", message = "Invalid name!")
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "mainService", cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<SubService> subServicesList;

    public MainService(String name) {
        this.name = name;
    }
}
