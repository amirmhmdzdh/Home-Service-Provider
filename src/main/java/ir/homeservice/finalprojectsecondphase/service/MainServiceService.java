package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.model.service.MainService;
import ir.homeservice.finalprojectsecondphase.repository.MainServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MainServiceService {
    private final MainServiceRepository mainServiceRepository;

    public Optional<MainService> findByName(String mainService) {
        return mainServiceRepository.findByName(mainService);
    }

    public void save(MainService mainService) {
        mainServiceRepository.save(mainService);
    }
}
