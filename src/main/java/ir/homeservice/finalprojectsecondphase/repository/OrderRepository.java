package ir.homeservice.finalprojectsecondphase.repository;

import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import ir.homeservice.finalprojectsecondphase.model.user.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> , JpaSpecificationExecutor<Orders> {


    @Query("FROM Orders o JOIN o.subServices s JOIN s.specialistList sp WHERE sp = :specialist")
    List<Orders> findOrdersBySpecialist(Specialist specialist);
}
