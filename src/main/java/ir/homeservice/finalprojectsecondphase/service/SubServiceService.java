package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.SubServicesIsNotExistException;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.service.SubService;
import ir.homeservice.finalprojectsecondphase.model.user.Customer;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.repository.SubServicesRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubServiceService {

    private final UsersService usersService;
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

    public List<SubService> historyOfSubServicesForCurrentUser(String email) {
        if (Objects.equals(email, "admin")) return null;
        Specification<SubService> subServiceByUser = findSubServiceByUser(email);
        return subServicesRepository.findAll(subServiceByUser);
    }

    private Specification<SubService> findSubServiceByUser(String email) {

        return (root, query, criteriaBuilder) -> {

            if (usersService.findByEmail(email).get().getRole().equals(Role.SPECIALIST)) {
                Join<Offer, Specialist> specialistJoin = root.join("specialistList", JoinType.INNER);
                return criteriaBuilder.equal(specialistJoin.get("email"), email);

            } else if (usersService.findByEmail(email).get().getRole().equals(Role.CUSTOMER)) {
                Join<SubService, Orders> subServiceOrderJoin = root.join("ordersList", JoinType.INNER);
                Join<Orders, Customer> customerJoin = subServiceOrderJoin.join("customer", JoinType.INNER);
                return criteriaBuilder.equal(customerJoin.get("email"), email);

            } else
                return null;
        };
    }




}
