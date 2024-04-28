package com.ua.yushchenko.tabakabot.builder.ui.admin;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COMPLETED_ORDER_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDERED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.PLANNED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.PROCESSING_ORDERS_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.mergeBotCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.InlineButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.service.OrderService;
import com.ua.yushchenko.tabakabot.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessOrderMenuBuilder {

    @NonNull
    private final InlineButtonBuilder buttonBuilder;
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
        final List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(buildPlannedButtons());
        buttons.add(buildOrderedButtons());
        buttons.add(buildBackToStartButtons());

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text("Processing orders panel")
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(buttons)
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

        final List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(buildRejectMenuButtons());
        buttons.add(buildBackToProcessingOrderButtons());

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToPlannedOrders,
                                                 "List of users who have planned to do an order"))
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(buttons)
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

        final List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(buildCompletedMenuButtons());
        buttons.add(buildBackToProcessingOrderButtons());

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToOrdered, "List of users who have ordered to do an order"))
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(buttons)
                                                               .build())
                              .build();
    }

    public EditMessageText buildRejectOrderMenu(final Long chatId, final Integer messageId) {
        final Set<Long> userIdsToPlannedOrders = getUserIdsByOrderStatus(OrderStatus.PLANNED);

        final List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        final List<InlineKeyboardButton> keyboardButtons = userIdsToPlannedOrders.stream()
                                                                                 .map(this::buildRemoveRejectedItemsButton)
                                                                                 .toList();

        buttons.add(keyboardButtons);
        buttons.add(buildBackToPlannedButtons());

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToPlannedOrders,
                                                 "List of users who have planned to do an order"))
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(buttons)
                                                               .build())
                              .build();
    }

    public EditMessageText buildCompletedOrderMenu(final Long chatId, final Integer messageId) {
        final Set<Long> userIdsToOrderedOrders = getUserIdsByOrderStatus(OrderStatus.ORDERED);

        final List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        final List<InlineKeyboardButton> keyboardButtons = userIdsToOrderedOrders.stream()
                                                                                 .map(this::buildRemoveCompletedItemsButton)
                                                                                 .toList();

        buttons.add(keyboardButtons);
        buttons.add(buildBackToOrderedButtons());

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildMessage(userIdsToOrderedOrders,
                                                 "List of users who have ordered to do an order"))
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(buttons)
                                                               .build())
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

    private List<InlineKeyboardButton> buildPlannedButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":page_with_curl: Planned"),
                                                         mergeBotCommand(PROCESSING_ORDERS_MENU, PLANNED_MENU)));
    }

    private List<InlineKeyboardButton> buildRejectMenuButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":wastebasket: Reject"),
                                                         mergeBotCommand(PROCESSING_ORDERS_MENU, PLANNED_MENU,
                                                                         REJECT_MENU)));
    }

    private InlineKeyboardButton buildRemoveRejectedItemsButton(final Long userId) {
        return buttonBuilder.buildButtonByString(String.valueOf(userId),
                                                 mergeBotCommand(PROCESSING_ORDERS_MENU, PLANNED_MENU, REJECT_MENU,
                                                                 userId));
    }

    private List<InlineKeyboardButton> buildOrderedButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":incoming_envelope: Ordered"),
                                                         mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU)));
    }

    private List<InlineKeyboardButton> buildCompletedMenuButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":wastebasket: Completed"),
                                                         mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU,
                                                                         COMPLETED_ORDER_MENU)));
    }

    private InlineKeyboardButton buildRemoveCompletedItemsButton(final Long userId) {
        return buttonBuilder.buildButtonByString(String.valueOf(userId),
                                                 mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU, COMPLETED_ORDER_MENU,
                                                                 userId));
    }

    private List<InlineKeyboardButton> buildBackToStartButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, START)));
    }

    private List<InlineKeyboardButton> buildBackToProcessingOrderButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, PROCESSING_ORDERS_MENU)));
    }

    private List<InlineKeyboardButton> buildBackToPlannedButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, PLANNED_MENU)));
    }

    private List<InlineKeyboardButton> buildBackToOrderedButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, ORDERED_MENU)));
    }
}
