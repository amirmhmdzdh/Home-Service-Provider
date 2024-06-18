package ir.homeservice.finalprojectsecondphase.repository;

import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubServicesRepository extends JpaRepository<SubService, Long> {

    Optional<SubService> findByName(String name);
}
