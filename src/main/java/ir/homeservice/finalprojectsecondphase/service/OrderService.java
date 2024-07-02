package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.model.offer.enums.OfferStatus;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.order.enums.OrderStatus;
import ir.homeservice.finalprojectsecondphase.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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

    public List<Orders> findAll() {
        return orderRepository.findAll();
    }

    public List<Orders> findByCustomerEmailAndOrderStatus(String email, OrderStatus orderStatus) {
        return orderRepository.findByCustomerEmailAndOrderStatus(email, orderStatus);
    }
}
