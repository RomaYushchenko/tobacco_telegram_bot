package com.ua.yushchenko.tabakabot.builder.ui.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Represents of builder for {@link EditMessageText} based on Tobacco Order Status
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoOrderStatusMenuBuilder {

    @NonNull
    private OrderService orderService;
    @NonNull
    private final CustomButtonBuilder buttonBuilder;

    public EditMessageText buildOrderStatusMenu(final Long chatId, final Integer messageId,
                                                final User user) {
        log.info("buildOrderStatusMenu.E: Building Tobacco Order Status menu...");

        final var ordersToOrderStatus = getOrdersToOrderStatusMap(user);

        if (CollectionUtils.isEmpty(ordersToOrderStatus)) {
            return EditMessageText.builder()
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .text("""
                                                You don't have any orders. For ordering items:\s
                                                \t 1) Click to 'back' button
                                                \t 2) Choose tobacco""")
                                  .replyMarkup(InlineKeyboardMarkup.builder()
                                                                   .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                                   .build())
                                  .build();
        }

        final StringBuilder orderStatusText = new StringBuilder();

        ordersToOrderStatus.forEach((orderStatus, orders) -> orderStatusText.append("You have ")
                                                                        .append(orders.size())
                                                                        .append(" orders in ")
                                                                        .append(orderStatus.toString())
                                                                        .append(" status;")
                                                                        .append("\n"));

        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .text(orderStatusText.toString())
                               .messageId(messageId)
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                                .build())
                               .build();

        log.info("buildOrderStatusMenu.X: Send message is created");
        return messageText;
    }

    private Map<OrderStatus, List<Order>> getOrdersToOrderStatusMap(final User user) {
        return orderService.getOrdersByUserId(user.getUserID())
                           .stream()
                           .collect(Collectors.groupingBy(Order::getOrderStatus));
    }
}
