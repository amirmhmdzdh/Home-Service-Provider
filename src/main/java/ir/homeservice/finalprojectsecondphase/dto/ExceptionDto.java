package ir.homeservice.finalprojectsecondphase.dto;

import java.time.LocalDateTime;

public record ExceptionDto(String message,
                           LocalDateTime localDateTime) {
}
