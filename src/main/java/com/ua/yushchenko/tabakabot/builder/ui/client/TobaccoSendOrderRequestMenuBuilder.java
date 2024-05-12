package com.ua.yushchenko.tabakabot.builder.ui.client;

import java.util.List;

import com.ua.yushchenko.tabakabot.builder.OrderListContextBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.OrderListContext;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    private final OrderService orderService;
    @NonNull
    private final CustomButtonBuilder buttonBuilder;
    @NonNull
    private final OrderListContextBuilder orderListContextBuilder;

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

        final var orderListContexts = orderListContextBuilder.buildOrderListContexts(userID, userOrders);

        final String orderList = buildOrderListByAllOrders(orderListContexts);

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

    private String buildOrderListByAllOrders(final List<OrderListContext> orderListContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        orderListBuilder.append(buildOrderList(orderListContexts))
                        .append("-------------------------------------")
                        .append("\n")
                        .append(buildPrice(orderListContexts))
                        .append("\n");

        return orderListBuilder.toString();
    }

    private String buildOrderList(final List<OrderListContext> orderListContexts) {
        final StringBuilder orderListBuilder = new StringBuilder();

        orderListContexts.forEach(orderListContext -> {
            orderListBuilder.append("\t\t\t")
                            .append("- ")
                            .append(orderListContext.getItemType().getItemString())
                            .append(" ")
                            .append(orderListContext.getDescription());

            final int size = orderListContext.getCount();

            if (size > 1) {
                orderListBuilder.append(" (x")
                                .append(size)
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
}
