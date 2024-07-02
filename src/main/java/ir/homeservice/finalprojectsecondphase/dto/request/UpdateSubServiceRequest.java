package ir.homeservice.finalprojectsecondphase.dto.request;

public record UpdateSubServiceRequest(Long id,
                                      String name,
                                      String description,
                                      Long basePrice) {
}
