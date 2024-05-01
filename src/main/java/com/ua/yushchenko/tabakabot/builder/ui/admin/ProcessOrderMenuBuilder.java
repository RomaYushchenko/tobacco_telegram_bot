package com.ua.yushchenko.tabakabot.builder.ui.admin;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.service.OrderService;
import com.ua.yushchenko.tabakabot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessOrderMenuBuilder {

    @NonNull
    private final CustomButtonBuilder buttonBuilder;
    @NonNull
    private final OrderService orderService;
    @NonNull
    private final UserService userService;

    /**
     * Builds Processing Order menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message to edit
     * @return {@link EditMessageText} with Processing Order menu
     */
    public EditMessageText buildProcessingOrdersMenu(final Long chatId, final Integer messageId) {
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text("Processing orders panel")
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(
                                                                       buttonBuilder.buildKeyBoardToProcessingOrdersMenu())
                                                               .build())
                              .build();
    }

    /**
     * Builds Planned menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message to edit
     * @return {@link EditMessageText} with Planned menu
     */
    public EditMessageText buildPlannedMenu(final Long chatId, final Integer messageId) {

        final Set<Long> userIdsToPlannedOrders = getUserIdsByOrderStatus(OrderStatus.PLANNED);

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToPlannedOrders,
                                                 "List of users who have planned to do an order"))
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(
                                                                       buttonBuilder.buildKeyBoardToAdminPlannedMenu())
                                                               .build())
                              .build();
    }

    /**
     * Builds Ordered menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message to edit
     * @return {@link EditMessageText} with Ordered menu
     */
    public EditMessageText buildOrderedMenu(final Long chatId, final Integer messageId) {
        final Set<Long> userIdsToOrdered = getUserIdsByOrderStatus(OrderStatus.ORDERED);

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToOrdered, "List of users who have ordered to do an order"))
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(
                                                                       buttonBuilder.buildKeyBoardToAdminOrderedMenu())
                                                               .build())
                              .build();
    }

    /**
     * Builds Reject Order menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message to edit
     * @return {@link EditMessageText} with Reject Order menu
     */
    public EditMessageText buildRejectOrderMenu(final Long chatId, final Integer messageId) {
        final Set<Long> userIdsToPlannedOrders = getUserIdsByOrderStatus(OrderStatus.PLANNED);

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buttonBuilder.buildKeyBoardToAdminRejectOrderMenu(
                                                            userIdsToPlannedOrders))
                                                    .build();
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToPlannedOrders,
                                                 "List of users who have planned to do an order"))
                              .replyMarkup(replyMarkup)
                              .build();
    }

    /**
     * Builds Completed Order menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message to edit
     * @return {@link EditMessageText} with Completed Order menu
     */
    public EditMessageText buildCompletedOrderMenu(final Long chatId, final Integer messageId) {
        final Set<Long> userIdsToOrderedOrders = getUserIdsByOrderStatus(OrderStatus.ORDERED);

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buttonBuilder.buildKeyBoardToAdminCompletedOrderMenu(
                                                            userIdsToOrderedOrders))
                                                    .build();
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToOrderedOrders,
                                                 "List of users who have ordered to do an order"))
                              .replyMarkup(replyMarkup)
                              .build();
    }

    private Set<Long> getUserIdsByOrderStatus(final OrderStatus orderStatus) {
        return orderService.getOrderByStatus(orderStatus)
                           .stream()
                           .map(Order::getUserId)
                           .collect(Collectors.toSet());
    }

    private String buildMessage(final Set<Long> userIds, final String test) {
        final StringBuilder users = new StringBuilder();

        users.append(test)
             .append("\n");

        for (final Long userId : userIds) {
            final User user = userService.getUserById(userId);

            if (Objects.isNull(user)) {
                continue;
            }

            users.append(user.getUserID())
                 .append(") ")
                 .append(user.getLinkName())
                 .append("\n");
        }

        return users.toString();
    }
}
