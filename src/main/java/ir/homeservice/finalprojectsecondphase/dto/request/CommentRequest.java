package ir.homeservice.finalprojectsecondphase.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CommentRequest(

        String textComment,
        @Max(5)
        @Min(0)
        Double star) {
}
