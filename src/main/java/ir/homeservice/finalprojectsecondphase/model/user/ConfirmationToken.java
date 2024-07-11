package ir.homeservice.finalprojectsecondphase.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer tokenId;

    String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;

    boolean isActive;

    @ManyToOne
    @JoinColumn(name = "users_id")
    Users users;

    public ConfirmationToken(Users users, boolean isActive) {
        this.users = users;
        this.isActive = isActive;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }
}