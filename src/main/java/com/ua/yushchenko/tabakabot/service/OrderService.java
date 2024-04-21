package com.ua.yushchenko.tabakabot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.model.mapper.OrderMapper;
import com.ua.yushchenko.tabakabot.model.persistence.OrderDb;
import com.ua.yushchenko.tabakabot.repository.OrderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service that exposes the base functionality for interacting with {@link Order} data
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class OrderService {

    @NonNull
    private final OrderRepository orderRepository;
    @NonNull
    private final OrderMapper orderMapper;

    /**
     * Adds order by user and tobacco item
     *
     * @param userId        ID of user
     * @param tobaccoItemId ID of tobacco item
     * @return order by user and tobacco item
     */
    public Order addOrderToUser(final long userId, final long tobaccoItemId) {
        log.info("addOrderToUser.E: User {} add order item {}", userId, tobaccoItemId);

        final Order orderToCreate = Order.builder()
                                         .userId(userId)
                                         .tobaccoItemId(tobaccoItemId)
                                         .orderTime(LocalDateTime.now())
                                         .orderStatus(OrderStatus.PLANNED)
                                         .build();

        final OrderDb createdOrder = orderRepository.save(orderMapper.domainToDb(orderToCreate));
        final Order order = orderMapper.dbToDomain(createdOrder);

        log.info("addOrderToUser.X: User {} add order {}", userId, order);
        return order;
    }

    /**
     * Gets list of {@link Order} by user ID
     *
     * @param userId ID of user
     * @return list of {@link Order} by user ID
     */
    public List<Order> getOrdersByUserId(final Long userId) {
        log.info("getOrdersByUserId.E: User ID: {}", userId);

        List<Order> orders = orderRepository.findAllByUserId(userId)
                                            .stream()
                                            .map(orderMapper::dbToDomain)
                                            .collect(Collectors.toList());

        log.info("getOrdersByUserId.X: User ID: {}, Orders: {}", userId, orders);
        return orders;
    }

    /**
     * Gets list of Planned {@link Order}s by user ID
     *
     * @param userId ID of user
     * @return list of Planned {@link Order}s by user ID
     */
    public List<Order> getPlannedOrdersByUserId(final Long userId) {
        log.info("getPlannedOrdersByUserId.E: User ID: {}", userId);

        List<Order> orders = orderRepository.findAllByUserIdAndOrderStatus(userId, OrderStatus.PLANNED)
                                            .stream()
                                            .map(orderMapper::dbToDomain)
                                            .collect(Collectors.toList());

        log.info("getPlannedOrdersByUserId.X: User ID: {}, Orders: {}", userId, orders);
        return orders;
    }

    /**
     * Gets list of Planned {@link Order}s by user ID
     *
     * @param userId ID of user
     * @return list of Planned {@link Order}s by user ID
     */
    public List<Order> getOrderedOrdersByUserId(final Long userId) {
        log.info("getOrderedOrdersByUserId.E: User ID: {}", userId);

        List<Order> orders = orderRepository.findAllByUserIdAndOrderStatus(userId, OrderStatus.ORDERED)
                                            .stream()
                                            .map(orderMapper::dbToDomain)
                                            .collect(Collectors.toList());

        log.info("getOrderedOrdersByUserId.X: User ID: {}, Orders: {}", userId, orders);
        return orders;
    }

    /**
     * Remove {@link Order} by user and tobacco item IDs
     *
     * @param userId        ID of user
     * @param tobaccoItemId ID of tobacco item
     */
    public void removeOrder(final Long userId, final Long tobaccoItemId) {
        log.info("removeOrder.E: User ID: {}, tobacco item ID: {}", userId, tobaccoItemId);

        final OrderDb orderDb = orderRepository.findAllByUserIdAndAndTobaccoItemId(userId, tobaccoItemId)
                                               .stream()
                                               .findFirst()
                                               .orElse(null);

        if (Objects.nonNull(orderDb)) {
            orderRepository.delete(orderDb);
            log.info("removeOrder.X: User ID: {}, tobacco item ID: {}, order: {}", userId, tobaccoItemId, orderDb);
        } else {
            log.warn("removeOrder.X: Order doesn't find  fot user ID: {}, tobacco item ID: {}", userId, tobaccoItemId);
        }

    }

    public void updateOrders(final List<Order> ordersByUserId) {
        ordersByUserId.forEach(order -> orderRepository.save(orderMapper.domainToDb(order)));
    }

    public List<Order> getAllOrders(){
        final List<Order> orders = new ArrayList<>();

        orderRepository.findAll()
                         .forEach(orderDb -> orders.add(orderMapper.dbToDomain(orderDb)));

        return orders;
    }
}
