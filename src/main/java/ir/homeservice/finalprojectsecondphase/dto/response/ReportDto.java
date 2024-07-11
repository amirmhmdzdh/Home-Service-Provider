package ir.homeservice.finalprojectsecondphase.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class ReportDto {

    LocalDateTime creationDate;

    Long requestOfOrders;

    Long doneOrders;
}