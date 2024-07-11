package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.OrderHistoryDto;
import ir.homeservice.finalprojectsecondphase.dto.response.FilterUserResponse;
import ir.homeservice.finalprojectsecondphase.exception.NotFoundException;
import ir.homeservice.finalprojectsecondphase.model.offer.Offer;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.repository.OrderRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UsersService usersService;

    public Orders chooseOffer(Orders orders, Long offerId) {
        orders.getOfferList().forEach(offer -> {
            if (offer.getId().equals(offerId))
                offer.setOfferStatus(OfferStatus.ACCEPTED);
        });
        orders.setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST_TO_COME);
        return orderRepository.save(orders);
    }


    public Orders save(Orders order) {
        return orderRepository.save(order);
    }

    public Optional<Orders> findById(Long orders) {
        return orderRepository.findById(orders);
    }


    public List<Orders> historyOfOrdersForUser(OrderHistoryDto dto) {
        return orderRepository.findAll(findFilteredOrdersForUser(dto));
    }

    private Specification<Orders> findFilteredOrdersForUser(OrderHistoryDto dto) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            if (dto.getStartDate() != null && dto.getEndDate() != null) {
                predicate = cb.and(predicate, cb.between(root.get("executionTime"), dto.getStartDate(), dto.getEndDate()));
            }

            if (dto.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("orderStatus"), dto.getStatus()));
            }

            if (dto.getMainServiceName() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("subServices").get("mainService").get("name"), dto.getMainServiceName()));
            }

            if (dto.getSubServiceName() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("subServices").get("name"), dto.getSubServiceName()));
            }
            return predicate;
        };
    }

    public List<Orders> findAll() {
        return orderRepository.findAll();
    }

    public List<Orders> findByCustomerEmailAndOrderStatus(String email, OrderStatus orderStatus) {
        return orderRepository.findByCustomerEmailAndOrderStatus(email, orderStatus);
    }

    public Long countOfOrders(String email) {
        if (Objects.equals(email, "admin")) return null;
        Specification<Orders> spec = countOrdersForSpecialist(email);
        return orderRepository.count(spec);
    }

    private Specification<Orders> countOrdersForSpecialist(String email) {
        return (root, query, criteriaBuilder) -> {

            if (usersService.findByEmail(email).get().getRole().equals(Role.SPECIALIST)) {
                Join<Orders, Offer> offerJoin = root.join("offerList", JoinType.INNER);
                return criteriaBuilder.equal(offerJoin.get("specialist").get("email"), email);

            } else if (usersService.findByEmail(email).get().getRole().equals(Role.CUSTOMER)) {
                return criteriaBuilder.equal(root.get("customer").get("email"), email);

            } else
                return null;
        };
    }

    public List<Orders> findAllBySpecialist(Specialist specialist, OrderStatus status) {
        if (orderRepository.findOrdersBySpecialist(specialist).isEmpty())
            throw new NotFoundException("THIS SPECIALIST DOESN'T HAVE ANY ORDER ! ");
        return orderRepository.findOrdersBySpecialist(specialist)
                .stream()
                .filter(
                        orders ->
                                orders.getOrderStatus().equals(status)
                )
                .toList();
    }

}
