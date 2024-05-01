package com.ua.yushchenko.tabakabot.builder.ui.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.ua.yushchenko.tabakabot.service.OrderService;
import com.ua.yushchenko.tabakabot.service.TobaccoService;
import com.ua.yushchenko.tabakabot.service.UserService;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
@RequiredArgsConstructor
public class OrderListBuilder {

    @NonNull
    private final OrderService orderService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final TobaccoService tobaccoService;
    @NonNull
    private final ItemService itemService;
    @NonNull
    private final CustomButtonBuilder buttonBuilder;

    public EditMessageText buildTobaccoAdminOrderListByUserMenu(final Long chatId, final Integer messageId) {
        final List<Order> allOrders = orderService.getAllOrders();

        final List<UserOrderContext> userOrderContexts = buildUserOrderContexts(allOrders);

        final String orderList = buildOrderListByUser(userOrderContexts);

        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("All orders by User: \n\n" + orderList)
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                                .build())
                               .build();

        return messageText;
    }

    public EditMessageText buildTobaccoAdminOrderListByAllUserMenu(final Long chatId, final Integer messageId) {
        final List<Order> allOrders = orderService.getAllOrders();

        final List<UserOrderContext> userOrderContexts = buildUserOrderContexts(allOrders);

        final String orderList = buildOrderListByAllOrders(userOrderContexts);


        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("All orders: \n\n" + orderList)
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                                .build())
                               .build();

        return messageText;
    }

    private String buildOrderListByAllOrders(final List<UserOrderContext> userOrderContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        final List<Order> allOrders =
                userOrderContexts.stream()
                                 .filter(userOrderContext -> !Objects.equals("y_romchik",
                                                                             userOrderContext.getUser().getLinkName()))
                                 .map(UserOrderContext::getOrderTobaccos)
                                 .flatMap(Collection::stream)
                                 .toList();

        final List<Order> adminOrders =
                userOrderContexts.stream()
                                 .filter(userOrderContext -> Objects.equals("y_romchik",
                                                                            userOrderContext.getUser().getLinkName()))
                                 .map(UserOrderContext::getOrderTobaccos)
                                 .flatMap(Collection::stream)
                                 .toList();

        if (!CollectionUtils.isEmpty(allOrders)) {
            orderListBuilder.append("=====================================")
                            .append("\n")
                            .append("----- CLIENTs")
                            .append("\n")
                            .append("\n")
                            .append(buildOrderList(allOrders))
                            .append("-------------------------------------")
                            .append("\n")
                            .append(buildPrice(allOrders))
                            .append("\n")
                            .append("\n");
        }

        if (!CollectionUtils.isEmpty(adminOrders)) {
            orderListBuilder.append("=====================================")
                            .append("\n")
                            .append("----- ADMINs")
                            .append("\n")
                            .append("\n")
                            .append(buildOrderList(adminOrders))
                            .append("-------------------------------------")
                            .append("\n")
                            .append(buildPrice(adminOrders))
                            .append("\n")
                            .append("\n");
        }

        return orderListBuilder.toString();
    }

    private String buildOrderListByUser(final List<UserOrderContext> userOrderContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        userOrderContexts.forEach(userOrderContext -> {
            final User user = userOrderContext.getUser();
            final List<Order> userOrders = userOrderContext.getOrderTobaccos();

            orderListBuilder.append("@")
                            .append(user.getLinkName())
                            .append(": ")
                            .append("\n")
                            .append(buildOrderList(userOrders))
                            .append("-------------------------------------")
                            .append("\n")
                            .append(buildPrice(userOrders))
                            .append("\n")
                            .append("\n");
        });

        return orderListBuilder.toString();
    }

    private String buildPrice(final List<Order> userOrders) {
        final Map<Long, List<Order>> tobaccoItemIdToOrder =
                userOrders.stream()
                          .collect(Collectors.groupingBy(Order::getTobaccoItemId));

        final Map<Long, List<Item>> tobaccoItemIdToTobaccoItem =
                itemService.getItemsByIds(new ArrayList<>(tobaccoItemIdToOrder.keySet()))
                           .stream()
                           .collect(Collectors.groupingBy(Item::getItemId));

        final Map<ItemType, List<Tobacco>> tobaccoToType =
                tobaccoService.getAllTobacco()
                              .stream()
                              .collect(Collectors.groupingBy(Tobacco::getTobaccoName));

        final List<PriceContext> priceContexts = new ArrayList<>();

        tobaccoItemIdToOrder.forEach((tobaccoItemId, orders) -> {
            final int count = orders.size();

            final Item tobaccoItem = tobaccoItemIdToTobaccoItem.get(tobaccoItemId).get(0);
            final ItemType tobaccoType = tobaccoItem.getItemType();

            int costTobacco = 0;

            if (Objects.equals(tobaccoType, ItemType.COAL)) {
                costTobacco = 240;
            } else {
                final Tobacco tobacco = tobaccoToType.get(tobaccoType).get(0);
                costTobacco = getCostByWeight(tobacco, tobaccoItem.getWeight());
            }

            priceContexts.add(PriceContext.builder()
                                          .cost(costTobacco * count)
                                          .build());
        });

        final Integer cost = priceContexts.stream().mapToInt(PriceContext::getCost).sum();

        return "Cost: " + cost + " grn";
    }

    private int getCostByWeight(final Tobacco tobacco, final int weight) {
        return switch (weight) {
            case 50 -> tobacco.getCost25();
            case 100 -> tobacco.getCost100();
            case 250 -> tobacco.getCost250();
            default -> 0;
        };
    }

    private String buildOrderList(final List<Order> userOrders) {
        final StringBuilder orderListBuilder = new StringBuilder();

        AtomicInteger countOrder = new AtomicInteger(0);

        userOrders.stream()
                  .collect(Collectors.groupingBy(Order::getTobaccoItemId))
                  .forEach((tobaccoItemId, orders) -> {
                      final var tobaccoItem = itemService.getItemById(tobaccoItemId);

                      orderListBuilder.append("\t\t\t")
                                      .append(countOrder.incrementAndGet())
                                      .append(") ")
                                      .append(tobaccoItem.getItemType().getItemString())
                                      .append(" ")
                                      .append(tobaccoItem.getDescription());

                      final int size = orders.size();

                      if (size > 1) {
                          orderListBuilder.append(" (x")
                                          .append(size)
                                          .append(")");
                      }

                      orderListBuilder.append("\n");
                  });

        return orderListBuilder.toString();
    }

    private List<UserOrderContext> buildUserOrderContexts(final List<Order> allOrders) {
        final List<UserOrderContext> userOrderContexts = new ArrayList<>();

        allOrders.stream()
                 .filter(order -> Objects.equals(order.getOrderStatus(), OrderStatus.ORDERED))
                 .collect(Collectors.groupingBy(Order::getUserId))
                 .forEach((userId, orders) -> {
                     final User user = userService.getUserById(userId);


                     final var userOrderContext = UserOrderContext.builder()
                                                                  .user(user)
                                                                  .orderTobaccos(orders)
                                                                  .build();

                     userOrderContexts.add(userOrderContext);
                 });

        return userOrderContexts;
    }

    @Value
    @Builder(toBuilder = true)
    private static class PriceContext {

        int cost;
    }

    @Value
    @Builder(toBuilder = true)
    private static class UserOrderContext {

        User user;
        List<Order> orderTobaccos;
    }
}
