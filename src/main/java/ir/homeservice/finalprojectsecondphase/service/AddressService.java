package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.AddressRequest;
import ir.homeservice.finalprojectsecondphase.model.address.Address;
import ir.homeservice.finalprojectsecondphase.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;

    public void createAddress(Address address) {
        addressRepository.save(address);
    }
}
