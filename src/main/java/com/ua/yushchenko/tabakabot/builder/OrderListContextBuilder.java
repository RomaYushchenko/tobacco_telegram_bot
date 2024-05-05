package com.ua.yushchenko.tabakabot.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.OrderListContext;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.ua.yushchenko.tabakabot.service.TobaccoService;
import com.ua.yushchenko.tabakabot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListContextBuilder {

    @NonNull
    private final ItemService itemService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final TobaccoService tobaccoService;

    public List<OrderListContext> buildOrderListContexts(final long userId, final List<Order> userOrders) {
        final Map<Long, List<Order>> ordersToTobaccoId =
                userOrders.stream()
                          .collect(Collectors.groupingBy(Order::getTobaccoItemId));

        final Map<Long, Item> itemToId =
                itemService.getItemsByIds(ordersToTobaccoId.keySet())
                           .stream()
                           .collect(Collectors.toMap(Item::getItemId, Function.identity()));

        final Map<ItemType, List<Tobacco>> tobaccoToType =
                tobaccoService.getAllTobacco()
                              .stream()
                              .collect(Collectors.groupingBy(Tobacco::getTobaccoName));

        final User user = userService.getUserById(userId);

        final List<OrderListContext> orderListContexts = new ArrayList<>();

        ordersToTobaccoId.forEach((tobaccoItemId, orders) -> {
            final var tobaccoItem = itemToId.get(tobaccoItemId);

            final ItemType itemType = tobaccoItem.getItemType();
            final int weight = tobaccoItem.getWeight();
            final int cost = getCostTobacco(itemType, weight, tobaccoToType) * orders.size();

            final var orderListContext = OrderListContext.builder()
                                                         .tobaccoItemId(tobaccoItemId)
                                                         .user(user)
                                                         .itemType(itemType)
                                                         .description(tobaccoItem.getDescription())
                                                         .wight(weight)
                                                         .count(orders.size())
                                                         .cost(cost)
                                                         .build();

            orderListContexts.add(orderListContext);
        });

        return orderListContexts;
    }

    private int getCostTobacco(final ItemType tobaccoType,
                               final int weight,
                               final Map<ItemType, List<Tobacco>> tobaccoToType) {
        if (Objects.equals(tobaccoType, ItemType.COAL)) {
            return 240;
        } else {
            final Tobacco tobacco = tobaccoToType.get(tobaccoType).get(0);
            return getCostByWeight(tobacco, weight);
        }
    }

    private int getCostByWeight(final Tobacco tobacco, final int weight) {
        return switch (weight) {
            case 50 -> tobacco.getCost25();
            case 100 -> tobacco.getCost100();
            case 250 -> tobacco.getCost250();
            default -> 0;
        };
    }
}
