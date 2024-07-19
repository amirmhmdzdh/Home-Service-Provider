package ir.homeservice.finalprojectsecondphase.model.user;

import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.enums.SpecialistStatus;
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
public class Specialist extends Users {
    @Lob
    private byte[] image;


    private Double star;


    private Long credit;


    private Integer doneOrders;


    @Enumerated(value = EnumType.STRING)
    private SpecialistStatus status;


    @ManyToMany(mappedBy = "specialistList", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @ToString.Exclude
    private List<SubService> subServicesList;


    @OneToMany(mappedBy = "specialist",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private List<Offer> offerList;

    public void addSubServices(SubService subServices) {
        this.subServicesList.add(subServices);
        subServices.getSpecialistList().add(this);
    }

    public void deleteSubServices(SubService subServices) {
        this.subServicesList.remove(subServices);
        subServices.getSpecialistList().remove(this);
    }

    public void delay(double hours) {
        double updateStar = star - hours;
        checkRate(updateStar);
    }

    private void checkRate(double updatedStar) {
        if (updatedStar < 0) {
            setStatus(SpecialistStatus.AWAITING);
            setStar(0d);
        } else
            setStar(updatedStar);
    }


}
