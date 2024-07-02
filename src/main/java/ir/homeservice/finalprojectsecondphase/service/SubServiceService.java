package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.SubServicesIsNotExistException;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.repository.SubServicesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubServiceService {
    private final SubServicesRepository subServicesRepository;

    public SubService save(SubService subService) {
        return subServicesRepository.save(subService);
    }

    public Optional<SubService> findById(Long service) {
        return subServicesRepository.findById(service);
    }

    public Optional<SubService> findByName(String subServicesName) {
        return subServicesRepository.findByName(subServicesName);
    }

    public List<SubService> findAllSubService() {
        List<SubService> subServiceList = subServicesRepository.findAll();
        if (subServiceList.isEmpty())
            throw new SubServicesIsNotExistException("there are no subServices!");
        return subServiceList;
    }
}
