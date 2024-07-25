package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.dto.request.OrderHistoryDto;
import ir.homeservice.finalprojectsecondphase.exception.NotFoundException;
import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import ir.homeservice.finalprojectsecondphase.repository.OrderRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

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
        return (root, query, builder) -> {

            Predicate predicate = builder.conjunction();

            if (dto.getStartDate() != null && dto.getEndDate() != null) {
                predicate = builder.and(predicate,
                        builder.between(root.get("executionTime"), dto.getStartDate(), dto.getEndDate()));
            }

            if (dto.getStatus() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("orderStatus"), dto.getStatus()));
            }

            if (dto.getMainServiceName() != null) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("subServices").get("mainService").get("name"), dto.getMainServiceName()));
            }

            if (dto.getSubServiceName() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("subServices").get("name"), dto.getSubServiceName()));
            }
            return predicate;
        };
    }

    public List<Orders> findAllBySpecialist(Specialist specialist, OrderStatus status) {
        if (orderRepository.findOrdersBySpecialist(specialist).isEmpty())
            throw new NotFoundException("THIS SPECIALIST DOESN'T HAVE ANY ORDER ! ");
        return orderRepository.findOrdersBySpecialist(specialist).stream()
                .filter(orders -> orders.getOrderStatus().equals(status))
                .toList();
    }
}
