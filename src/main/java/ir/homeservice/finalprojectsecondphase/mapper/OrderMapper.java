package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.OrdersRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.OrderResponse;
import ir.homeservice.finalprojectsecondphase.model.order.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Orders requestDtoToModelToAddOrder(OrdersRequest request);

    OrderResponse modelToAddOrder(Orders orders);
}
