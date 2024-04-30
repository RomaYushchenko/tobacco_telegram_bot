package com.ua.yushchenko.tabakabot.builder.ui.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.ua.yushchenko.tabakabot.service.OrderService;
import com.ua.yushchenko.tabakabot.service.TobaccoService;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


/**
 * Represents of builder for {@link EditMessageText} based on SOrder Request Menu
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoSendOrderRequestMenuBuilder {

    @NonNull
    private final ItemService itemService;
    @NonNull
    private final OrderService orderService;
    @NonNull
    private final CustomButtonBuilder buttonBuilder;
    @NonNull
    private final TobaccoService tobaccoService;

    /**
     * Build {@link EditMessageText} for send order request menu
     *
     * @param chatId    ID of chat
     * @param messageId ID of message
     * @param user      current user
     * @return {@link EditMessageText} for send order request menu
     */
    public EditMessageText buildSendOrderRequestMenu(final Long chatId, final Integer messageId, final User user) {
        log.info("buildSendOrderRequestMenu.E: Building Tobacco Send Order Request menu...");

        final long userID = user.getUserID();
        final String fullName = user.getLinkName();

        final var userOrders = orderService.getOrderedOrdersByUserId(userID);

        if (CollectionUtils.isEmpty(userOrders)) {
            return EditMessageText.builder()
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .text("""
                                                You don't have order items to sent order request. For ordering items:\s
                                                \t 1) Click to 'back' button
                                                \t 2) Choose tobacco""")
                                  .replyMarkup(InlineKeyboardMarkup.builder()
                                                                   .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                                   .build())
                                  .build();
        }

        final List<OrderRequestContext> orderRequestContexts = buildOrderRequestContext(userOrders);

        final String orderList = buildOrderListByAllOrders(orderRequestContexts);

        final var messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("Hello @" + fullName + "\n" +
                                             "Your request order sent to admin " +
                                             "and it's currently in order processing mode. \n\n" +
                                             "Your order list: \n\n" + orderList)
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                                .build())
                               .build();

        log.info("buildSendOrderRequestMenu.X: Send message is created");
        return messageText;
    }

    private String buildOrderListByAllOrders(final List<OrderRequestContext> userOrderContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        orderListBuilder.append(buildOrderList(userOrderContexts))
                        .append("-------------------------------------")
                        .append("\n")
                        .append(buildPrice(userOrderContexts))
                        .append("\n");

        return orderListBuilder.toString();
    }

    private String buildOrderList(final List<OrderRequestContext> orderRequestContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        orderRequestContexts.stream()
                            .collect(Collectors.groupingBy(OrderRequestContext::getTobaccoItemId))
                            .forEach((tobaccoItemId, orders) -> {
                                final OrderRequestContext orderRequestContext = orders.get(0);

                                orderListBuilder.append("\t\t\t")
                                                .append("- ")
                                                .append(orderRequestContext.getTobaccoType().getItemString())
                                                .append(" ")
                                                .append(orderRequestContext.getTaste());

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

    private String buildPrice(final List<OrderRequestContext> orderRequestContexts) {
        final Map<Long, List<OrderRequestContext>> tobaccoItemIdToOrder =
                orderRequestContexts.stream()
                                    .collect(Collectors.groupingBy(OrderRequestContext::getTobaccoItemId));

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
                costTobacco = 221;
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

    private List<OrderRequestContext> buildOrderRequestContext(final List<Order> userOrders) {
        final List<OrderRequestContext> orderRequestContexts = new ArrayList<>();

        final Set<Long> tobaccoIds = userOrders.stream()
                                               .map(Order::getTobaccoItemId)
                                               .collect(Collectors.toSet());

        final Map<Long, Item> itemToId =
                itemService.getItemsByIds(tobaccoIds)
                           .stream()
                           .collect(Collectors.toMap(Item::getItemId, Function.identity()));

        userOrders.forEach((userOrder) -> {
            final Item tobaccoItem = itemToId.get(userOrder.getTobaccoItemId());

            orderRequestContexts.add(OrderRequestContext.builder()
                                                        .orderId(userOrder.getOrderId())
                                                        .tobaccoItemId(userOrder.getTobaccoItemId())
                                                        .tobaccoType(tobaccoItem.getItemType())
                                                        .taste(tobaccoItem.getDescription())
                                                        .wight(tobaccoItem.getWeight())
                                                        .build());
        });

        return orderRequestContexts;
    }

    @Value
    @Builder(toBuilder = true)
    private static class OrderRequestContext {

        long orderId;
        long tobaccoItemId;
        ItemType tobaccoType;
        String taste;
        int wight;
    }

    @Value
    @Builder(toBuilder = true)
    private static class PriceContext {

        int cost;
    }
}
