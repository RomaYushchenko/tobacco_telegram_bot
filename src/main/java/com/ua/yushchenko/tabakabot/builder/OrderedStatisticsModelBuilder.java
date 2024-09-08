package com.ua.yushchenko.tabakabot.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.OrderedStatisticsModel;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.ua.yushchenko.tabakabot.service.OrderService;
import com.ua.yushchenko.tabakabot.service.TobaccoService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Represents of builder for {@link OrderedStatisticsModel} based on orders
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderedStatisticsModelBuilder {

    @NonNull
    private OrderService orderService;
    @NonNull
    private ItemService itemService;
    @NonNull
    private TobaccoService tobaccoService;

    /**
     * Build list of global ordered statistics model
     *
     * @return list of global ordered statistics model
     */
    public List<OrderedStatisticsModel> buildGlobal() {
        final Map<Long, Long> tobaccoItemIdToCount = orderService.getOrderByStatus(OrderStatus.COMPLETED)
                                                                 .stream()
                                                                 .collect(Collectors.groupingBy(Order::getTobaccoItemId,
                                                                                                Collectors.counting()));

        return buildOrderedStatisticsModel(tobaccoItemIdToCount);
    }

    /**
     * Build list of ordered statistics model by user
     *
     * @param user instance of user
     * @return list of ordered statistics model by user
     */
    public List<OrderedStatisticsModel> buildUser(final User user) {
        final Map<Long, Long> tobaccoItemIdToCount = orderService.getCompletedOrdersByUserId(user.getUserID())
                                                                 .stream()
                                                                 .collect(Collectors.groupingBy(Order::getTobaccoItemId,
                                                                                                Collectors.counting()));

        return buildOrderedStatisticsModel(tobaccoItemIdToCount);
    }

    private List<OrderedStatisticsModel> buildOrderedStatisticsModel(final Map<Long, Long> tobaccoItemIdToCount) {
        final var tobaccoItemIdToItem = itemService.getItemsByIds(tobaccoItemIdToCount.keySet())
                                                   .stream()
                                                   .collect(Collectors.toMap(Item::getItemId, Function.identity()));

        final var tobaccoToType = tobaccoService.getAllTobacco()
                                                .stream()
                                                .collect(Collectors.groupingBy(Tobacco::getTobaccoName));

        final List<OrderedStatisticsModel> orderedStatisticsModels = new ArrayList<>();

        tobaccoItemIdToCount.forEach((tobaccoItemId, count) -> {

            if (!tobaccoItemIdToItem.containsKey(tobaccoItemId)) {
                log.warn(
                        "buildUser.X: Skip building the OrderedStatisticsModel. Cannot find item by tobaccoItemId:{}",
                        tobaccoItemId);
                return;
            }

            final Item tobaccoItem = tobaccoItemIdToItem.get(tobaccoItemId);

            final ItemType itemType = tobaccoItem.getItemType();
            final int weight = tobaccoItem.getWeight();

            final Long cost = getCostTobacco(itemType, weight, tobaccoToType) * count;

            orderedStatisticsModels.add(OrderedStatisticsModel.builder()
                                                              .tobaccoItemId(tobaccoItemId)
                                                              .itemType(itemType)
                                                              .description(tobaccoItem.getDescription())
                                                              .cost(cost)
                                                              .count(count)
                                                              .build());
        });

        return orderedStatisticsModels;
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
