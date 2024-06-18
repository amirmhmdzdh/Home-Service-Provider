package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.repository.SubServicesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubServiceService {
    private final SubServicesRepository subServicesRepository;

    public void save(SubService subService) {
        subServicesRepository.save(subService);
    }

    public Optional<SubService> findById(Long service) {
        return subServicesRepository.findById(service);
    }

    public Optional<SubService> findByName(String subServicesName) {
        return subServicesRepository.findByName(subServicesName);
    }


}
