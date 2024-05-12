package com.ua.yushchenko.tabakabot.model.mapper;

import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.persistence.OrderDb;
import org.springframework.stereotype.Component;

/**
 * Represents mapping persistence to domain entity
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Component
public class OrderMapper {

    public Order dbToDomain(final OrderDb orderDb) {
        return Order.builder()
                    .orderId(orderDb.getOrderId())
                    .userId(orderDb.getUserId())
                    .tobaccoItemId(orderDb.getTobaccoItemId())
                    .orderTime(orderDb.getOrderTime())
                    .orderStatus(orderDb.getOrderStatus())
                    .build();
    }

    public OrderDb domainToDb(final Order order) {
        return OrderDb.builder()
                      .orderId(order.getOrderId())
                      .userId(order.getUserId())
                      .tobaccoItemId(order.getTobaccoItemId())
                      .orderTime(order.getOrderTime())
                      .orderStatus(order.getOrderStatus())
                      .build();
    }
}
