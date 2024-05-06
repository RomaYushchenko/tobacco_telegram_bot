package com.ua.yushchenko.tabakabot.builder.ui.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.OrderListContextBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.OrderListContext;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    private final CustomButtonBuilder buttonBuilder;
    @NonNull
    private final OrderListContextBuilder orderListContextBuilder;

    public EditMessageText buildTobaccoAdminOrderListByUserMenu(final Long chatId, final Integer messageId) {
        final List<Order> allOrders = getAllOrderedOrders();

        final var ordersToUserId = allOrders.stream()
                                            .collect(Collectors.groupingBy(Order::getUserId));

        final List<OrderListContext> orderListContexts = new ArrayList<>();

        ordersToUserId.forEach((userId, orders) -> {
            orderListContexts.addAll(orderListContextBuilder.buildOrderListContexts(userId, orders));
        });

        final String orderList = buildOrderListByUser(orderListContexts);

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
        final List<Order> allOrders = getAllOrderedOrders();

        final var ordersToUserId = allOrders.stream()
                                            .collect(Collectors.groupingBy(Order::getUserId));

        final List<OrderListContext> orderListContexts = new ArrayList<>();

        ordersToUserId.forEach((userId, orders) -> {
            orderListContexts.addAll(orderListContextBuilder.buildOrderListContexts(userId, orders));
        });

        final String orderList = buildOrderListByAllOrders(orderListContexts);


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

    private String buildOrderListByAllOrders(final List<OrderListContext> orderListContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        final List<OrderListContext> allOrders = getOrderListContextsOfClient(orderListContexts);
        final List<OrderListContext> adminOrders = getOrderListContextsOfAdmin(orderListContexts);

        if (!CollectionUtils.isEmpty(allOrders)) {
            orderListBuilder.append("=====================================")
                            .append("\n")
                            .append("\t\t\t\tCLIENTs")
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
                            .append("\t\t\t\t ADMINs")
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

    private String buildOrderListByUser(final List<OrderListContext> orderListContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        orderListContexts.stream()
                         .collect(Collectors.groupingBy(OrderListContext::getUser))
                         .forEach((user, userOrders) -> {
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

    private String buildOrderList(final List<OrderListContext> orderListContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        final AtomicInteger countOrder = new AtomicInteger(0);

        orderListContexts.stream()
                         .collect(Collectors.groupingBy(OrderListContext::getTobaccoItemId))
                         .forEach((tobaccoItemId, contexts) -> {

                             final var orderListContext = contexts.stream().findAny().orElse(null);

                             if (Objects.isNull(orderListContext)) {
                                 return;
                             }

                             orderListBuilder.append("\t\t\t")
                                             .append(countOrder.incrementAndGet())
                                             .append(") ")
                                             .append(orderListContext.getItemType().getItemString())
                                             .append(" ")
                                             .append(orderListContext.getDescription());

                             final int count = contexts.stream()
                                                       .mapToInt(OrderListContext::getCount)
                                                       .sum();

                             if (count > 1) {
                                 orderListBuilder.append(" (x")
                                                 .append(count)
                                                 .append(")");
                             }

                             orderListBuilder.append("\n");
                         });

        return orderListBuilder.toString();
    }

    private String buildPrice(final List<OrderListContext> orderListContexts) {
        final Integer cost = orderListContexts.stream()
                                              .mapToInt(OrderListContext::getCost)
                                              .sum();

        return "Cost: " + cost + " grn";
    }

    private List<OrderListContext> getOrderListContextsOfClient(final List<OrderListContext> orderListContexts) {
        return orderListContexts.stream()
                                .filter(context -> !Objects.equals("y_romchik", context.getUser().getLinkName()))
                                .toList();
    }

    private List<OrderListContext> getOrderListContextsOfAdmin(final List<OrderListContext> orderListContexts) {
        return orderListContexts.stream()
                                .filter(context -> Objects.equals("y_romchik", context.getUser().getLinkName()))
                                .toList();
    }

    private List<Order> getAllOrderedOrders() {
        return orderService.getAllOrders()
                           .stream()
                           .filter(order -> Objects.equals(order.getOrderStatus(), OrderStatus.ORDERED))
                           .toList();
    }
}
