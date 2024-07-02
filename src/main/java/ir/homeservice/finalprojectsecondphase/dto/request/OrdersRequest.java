package ir.homeservice.finalprojectsecondphase.dto.request;

import java.time.LocalDateTime;

public record OrdersRequest(Long subServiceId,
                            Long proposedPrice,
                            String description,
                            LocalDateTime workStartDate,
                            LocalDateTime workEndDate,
                            String addressProvince) {
}
