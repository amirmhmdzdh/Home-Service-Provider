package ir.homeservice.finalprojectsecondphase.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MainServiceResponse {
    private Long id;
    private String name;
    /**
     * DTO for {@link ir.homeservice.finalprojectsecondphase.model.service.SubService}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubServiceDto {
        private Long id;
        private String name;
    }
    private List<SubServiceDto> subServicesList;
}
