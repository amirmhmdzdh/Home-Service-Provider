package ir.homeservice.finalprojectsecondphase.dto.request;

public record SubServiceRequest(
        String name,
        String description,
        Long basePrice,
        MainServiceRequest mainService) {
}
