package ir.homeservice.finalprojectsecondphase.repository;

import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SubServicesRepository extends JpaRepository<SubService, Long>, JpaSpecificationExecutor<SubService> {

    Optional<SubService> findByName(String name);
}
