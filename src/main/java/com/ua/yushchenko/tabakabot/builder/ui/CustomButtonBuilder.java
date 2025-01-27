package com.ua.yushchenko.tabakabot.builder.ui;


import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COAL;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COMPLETED_ORDER_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GET_ALL_ORDERS;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GET_ALL_ORDERS_BY_USER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GET_ALL_USERS;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GLOBAL_ORDERED_STATISTICS_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDERED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDERED_STATISTICS_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER_LIST;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER_STATUS;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.PLANNED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.PROCESSING_ORDERS_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_ORDERED_ITEM;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_ORDERED_MENU_ITEM;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REMOVE_ORDER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.SEND_ORDER_REQUEST;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.USER_ORDERED_STATISTICS_MENU;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.mergeBotCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.OrderListContext;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Represents of builder for {@link InlineKeyboardButton}
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Component
@RequiredArgsConstructor
public class CustomButtonBuilder {

    @NonNull
    private final InlineButtonBuilder buttonBuilder;

    /**
     * Build list of {@link InlineKeyboardButton} for Tobacco menu
     *
     * @param tobaccos list of {@link Tobacco}
     * @return list of {@link InlineKeyboardButton} for Tobacco menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToTobaccoMenu(final List<Tobacco> tobaccos) {
        final List<List<InlineKeyboardButton>> mainMenuButtons = new ArrayList<>();

        mainMenuButtons.add(buildTobaccoButtons(tobaccos));
        mainMenuButtons.add(buildCoalItemButtons());
        mainMenuButtons.add(buildOrderListButtons());
        mainMenuButtons.add(buildSendOrderRequestButtons());
        mainMenuButtons.add(buildOrderStatusButtons());
        mainMenuButtons.add(buildOrderStatisticsButtons());

        return mainMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Order Coal menu
     *
     * @param coals list of {@link Item}
     * @return list of {@link InlineKeyboardButton} for Order Coal menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToOrderCoalMenu(final List<Item> coals) {
        final List<List<InlineKeyboardButton>> orderCoalMenuButtons = new ArrayList<>();

        orderCoalMenuButtons.addAll(getCoalItemButtonRows(coals));
        orderCoalMenuButtons.add(buildBackToStartButtons());

        return orderCoalMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Order List menu
     *
     * @return list of {@link InlineKeyboardButton} for Order List menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToOrderListMenu() {
        final List<List<InlineKeyboardButton>> orderListMenuButtons = new ArrayList<>();
        orderListMenuButtons.add(buildRemoveOrderButtons());
        orderListMenuButtons.add(buildBackToStartButtons());

        return orderListMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Remove Order List menu
     *
     * @param tobaccoItemIds list of ID of the items
     * @return list of {@link InlineKeyboardButton} for Remove Order List menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToRemoveOrderMenu(final List<Long> tobaccoItemIds) {
        final List<List<InlineKeyboardButton>> removeOrderMenuButtons = new ArrayList<>();

        removeOrderMenuButtons.addAll(getRemoveTobaccoOrderButtonRows(tobaccoItemIds));
        removeOrderMenuButtons.add(buildBackToOrderListButtons());

        return removeOrderMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Order Tobacco menu
     *
     * @param tobaccoItemIds    list of ID of the items
     * @param tobaccoBotCommand command of tobacco bot
     * @return list of {@link InlineKeyboardButton} for Order Tobacco menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToOrderTobaccoMenu(final List<Long> tobaccoItemIds,
                                                                            final TobaccoBotCommand tobaccoBotCommand) {
        final List<List<InlineKeyboardButton>> orderTobaccoMenuButtons = new ArrayList<>();

        orderTobaccoMenuButtons.addAll(getTobaccoOrderButtonRows(tobaccoItemIds, tobaccoBotCommand));
        orderTobaccoMenuButtons.add(buildBackToStartButtons());

        return orderTobaccoMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Tobacco menu
     *
     * @return list of {@link InlineKeyboardButton} for Admin Tobacco menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToAdminTobaccoMenu() {
        final List<List<InlineKeyboardButton>> mainMenuButtons = new ArrayList<>();

        mainMenuButtons.add(buildOrderListAdminButtons());
        mainMenuButtons.add(buildUserListAdminButtons());
        mainMenuButtons.add(buildProcessingOrderMenuAdminButtons());

        return mainMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Processing Orders menu
     *
     * @return list of {@link InlineKeyboardButton} for Admin Processing Orders menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToProcessingOrdersMenu() {
        final List<List<InlineKeyboardButton>> processingOrdersMenuButtons = new ArrayList<>();

        processingOrdersMenuButtons.add(buildPlannedButtons());
        processingOrdersMenuButtons.add(buildOrderedButtons());
        processingOrdersMenuButtons.add(buildBackToStartButtons());

        return processingOrdersMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Planned menu
     *
     * @return list of {@link InlineKeyboardButton} for Admin Planned menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToAdminPlannedMenu() {
        final List<List<InlineKeyboardButton>> plannedMenuButtons = new ArrayList<>();

        plannedMenuButtons.add(buildRejectMenuButtons());
        plannedMenuButtons.add(buildBackToProcessingOrderButtons());

        return plannedMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Ordered menu
     *
     * @return list of {@link InlineKeyboardButton} for Admin Ordered menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToAdminOrderedMenu() {
        final List<List<InlineKeyboardButton>> orderedMenuButtons = new ArrayList<>();

        orderedMenuButtons.add(buildCompletedMenuButtons());
        orderedMenuButtons.add(buildRejectedOrderedMenuButtons());
        orderedMenuButtons.add(buildBackToProcessingOrderButtons());

        return orderedMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Reject Ordered menu
     *
     * @param userId            ID of user
     * @param orderListContexts list of {@link OrderListContext}
     * @return list of {@link InlineKeyboardButton} for Admin Reject Ordered menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToAdminRejectOrderedMenu(final Long userId,
                                                                                  final List<OrderListContext> orderListContexts) {
        final List<List<InlineKeyboardButton>> rejectOrderedMenuButtons = new ArrayList<>();

        final List<Long> itemIds = orderListContexts.stream()
                                                    .map(OrderListContext::getTobaccoItemId)
                                                    .toList();

        rejectOrderedMenuButtons.addAll(getRejectedOrderedItemButtonRows(userId, itemIds));
        rejectOrderedMenuButtons.add(buildBackToProcessingOrderButtons());
        return rejectOrderedMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Reject Order menu
     *
     * @param userIds list of IDs of user
     * @return list of {@link InlineKeyboardButton} for Admin Reject Order menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToAdminRejectPlannedOrderMenu(final Set<Long> userIds) {
        final List<List<InlineKeyboardButton>> rejectOrderMenuButtons = new ArrayList<>();

        rejectOrderMenuButtons.addAll(getRemoveRejectedItemsButtonRows(new ArrayList<>(userIds)));
        rejectOrderMenuButtons.add(buildBackToPlannedButtons());

        return rejectOrderMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Completed Order menu
     *
     * @param userIds list of IDs of user
     * @return list of {@link InlineKeyboardButton} for Admin Completed Order menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToAdminCompletedOrderedOrderMenu(final Set<Long> userIds) {
        final List<List<InlineKeyboardButton>> completedOrderMenuButtons = new ArrayList<>();

        completedOrderMenuButtons.addAll(getRemoveCompletedItemsButtonRows(new ArrayList<>(userIds)));
        completedOrderMenuButtons.add(buildBackToOrderedButtons());

        return completedOrderMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Rejected Ordered Order menu
     *
     * @param userIds list of IDs of user
     * @return list of {@link InlineKeyboardButton} for Admin Rejected Ordered Order menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToAdminRejectedOrderedOrderMenu(final Set<Long> userIds) {
        final List<List<InlineKeyboardButton>> completedOrderMenuButtons = new ArrayList<>();

        completedOrderMenuButtons.addAll(getRejectOrderedItemsButtonRows(new ArrayList<>(userIds)));
        completedOrderMenuButtons.add(buildBackToOrderedButtons());

        return completedOrderMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Back to Start menu
     *
     * @return list of {@link InlineKeyboardButton} for Back to Start menu
     */
    public List<InlineKeyboardButton> buildBackToStartButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, START)));
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Order List menu
     *
     * @return list of {@link InlineKeyboardButton} for Order List menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToOrderedStatisticsMenu() {
        final List<List<InlineKeyboardButton>> orderListMenuButtons = new ArrayList<>();
        orderListMenuButtons.add(buildGlobalStatisticsButtons());
        orderListMenuButtons.add(buildUserStatisticsButtons());
        orderListMenuButtons.add(buildBackToStartButtons());

        return orderListMenuButtons;
    }

    /**
     * Build list of {@link InlineKeyboardButton} for Admin Planned menu
     *
     * @return list of {@link InlineKeyboardButton} for Admin Planned menu
     */
    public List<List<InlineKeyboardButton>> buildKeyBoardToGlobalOrderedStatisticsMenu() {
        final List<List<InlineKeyboardButton>> plannedMenuButtons = new ArrayList<>();

        plannedMenuButtons.add(buildBackToOrderedStatisticsMenuButtons());

        return plannedMenuButtons;
    }

    private List<InlineKeyboardButton> buildTobaccoButtons(final List<Tobacco> tobaccos) {
        return tobaccos.stream()
                       .map(tobacco -> buttonBuilder.buildButton(tobacco.getTobaccoName().getItemString(),
                                                                 tobacco.getTobaccoCommand()))
                       .toList();
    }

    private List<List<InlineKeyboardButton>> getCoalItemButtonRows(final List<Item> coals) {
        return Lists.partition(coals, 6)
                    .stream()
                    .map(this::buildRowButtonOfCoalOrder)
                    .toList();
    }

    private List<InlineKeyboardButton> buildRowButtonOfCoalOrder(final List<Item> coalsPart) {
        return coalsPart.stream()
                        .map(coal -> buttonBuilder.buildButtonByString(String.valueOf(coal.getDescription()),
                                                                       buildCoalOrderCommand(coal.getItemId())))
                        .toList();
    }

    private List<List<InlineKeyboardButton>> getRemoveTobaccoOrderButtonRows(final List<Long> tobaccoItemIds) {
        return Lists.partition(tobaccoItemIds, 6)
                    .stream()
                    .map(this::buildRowButtonOfRemoveOrder)
                    .toList();
    }

    private List<List<InlineKeyboardButton>> getRemoveRejectedItemsButtonRows(final List<Long> userIds) {
        return Lists.partition(userIds, 6)
                    .stream()
                    .map(this::buildRowButtonOfRemoveRejectedItems)
                    .toList();
    }

    private List<List<InlineKeyboardButton>> getRejectedOrderedItemButtonRows(final Long userId,
                                                                              final List<Long> orderIds) {
        return Lists.partition(orderIds, 6)
                    .stream()
                    .map(list -> buildRowButtonOfRejectedOrderedItems(userId, list))
                    .toList();
    }

    private List<InlineKeyboardButton> buildRowButtonOfRejectedOrderedItems(final Long userId,
                                                                            final List<Long> orderIds) {
        return orderIds.stream()
                       .map(orderId -> buildRejectedOrderedItemButton(userId, orderId))
                       .distinct()
                       .toList();
    }

    private List<InlineKeyboardButton> buildRowButtonOfRemoveRejectedItems(final List<Long> userIds) {
        return userIds.stream()
                      .map(this::buildRemoveRejectedItemsButton)
                      .distinct()
                      .toList();
    }

    private List<List<InlineKeyboardButton>> getRemoveCompletedItemsButtonRows(final List<Long> userIds) {
        return Lists.partition(userIds, 6)
                    .stream()
                    .map(this::buildRowButtonOfRemoveCompletedItems)
                    .toList();
    }

    private List<List<InlineKeyboardButton>> getRejectOrderedItemsButtonRows(final List<Long> userIds) {
        return Lists.partition(userIds, 6)
                    .stream()
                    .map(this::buildRowButtonOfRejectOrderedItems)
                    .toList();
    }

    private List<InlineKeyboardButton> buildRowButtonOfRemoveCompletedItems(final List<Long> userIds) {
        return userIds.stream()
                      .map(this::buildRemoveCompletedItemsButton)
                      .distinct()
                      .toList();
    }

    private List<InlineKeyboardButton> buildRowButtonOfRejectOrderedItems(final List<Long> userIds) {
        return userIds.stream()
                      .map(this::buildRejectOrderedItemsButton)
                      .distinct()
                      .toList();
    }

    private List<InlineKeyboardButton> buildRowButtonOfRemoveOrder(final List<Long> tobaccoItemIds) {
        return tobaccoItemIds.stream()
                             .map(tobaccoItemId ->
                                          buttonBuilder.buildButtonByString(String.valueOf(tobaccoItemId),
                                                                            mergeBotCommand(REMOVE_ORDER,
                                                                                            tobaccoItemId)))
                             .distinct()
                             .toList();
    }

    private List<List<InlineKeyboardButton>> getTobaccoOrderButtonRows(final List<Long> tobaccoItemIds,
                                                                       final TobaccoBotCommand tobaccoBotCommand) {
        return Lists.partition(tobaccoItemIds, 6)
                    .stream()
                    .map(tobaccoItemIdsPart -> buildRowButtonOfAddOrder(tobaccoItemIdsPart, tobaccoBotCommand))
                    .toList();
    }

    private List<InlineKeyboardButton> buildRowButtonOfAddOrder(final List<Long> tobaccoItemIds,
                                                                final TobaccoBotCommand tobaccoBotCommand) {
        return tobaccoItemIds.stream()
                             .map(tobaccoItemId ->
                                          buttonBuilder.buildButtonByString(
                                                  String.valueOf(tobaccoItemId),
                                                  buildTobaccoOrderCommand(tobaccoBotCommand, tobaccoItemId)))
                             .distinct()
                             .toList();
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

    private InlineKeyboardButton buildRejectedOrderedItemButton(final Long userId, final Long itemId) {
        return buttonBuilder.buildButtonByString(String.valueOf(itemId),
                                                 mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU,
                                                                 REJECT_ORDERED_ITEM,
                                                                 userId, itemId));
    }

    private List<InlineKeyboardButton> buildOrderedButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":incoming_envelope: Ordered"),
                                                         mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU)));
    }

    private List<InlineKeyboardButton> buildCompletedMenuButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":white_check_mark: Completed"),
                                                         mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU,
                                                                         COMPLETED_ORDER_MENU)));
    }

    private List<InlineKeyboardButton> buildRejectedOrderedMenuButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":wastebasket: Reject"),
                                                         mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU,
                                                                         REJECT_ORDERED_MENU_ITEM)));
    }

    private InlineKeyboardButton buildRemoveCompletedItemsButton(final Long userId) {
        return buttonBuilder.buildButtonByString(String.valueOf(userId),
                                                 mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU,
                                                                 COMPLETED_ORDER_MENU,
                                                                 userId));
    }

    private InlineKeyboardButton buildRejectOrderedItemsButton(final Long userId) {
        return buttonBuilder.buildButtonByString(String.valueOf(userId),
                                                 mergeBotCommand(PROCESSING_ORDERS_MENU, ORDERED_MENU,
                                                                 REJECT_ORDERED_MENU_ITEM,
                                                                 userId));
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

    private List<InlineKeyboardButton> buildCoalItemButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode("\uD83E\uDEA8 Coal"),
                                                 COAL));
    }

    private List<InlineKeyboardButton> buildOrderListButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":scroll: Order list"),
                                                 ORDER_LIST));
    }

    private List<InlineKeyboardButton> buildSendOrderRequestButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":incoming_envelope: Send Order"),
                                                 SEND_ORDER_REQUEST));
    }

    private List<InlineKeyboardButton> buildOrderStatusButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":hourglass_flowing_sand: Order Status"),
                                                 ORDER_STATUS));
    }

    private List<InlineKeyboardButton> buildOrderStatisticsButtons() {
        return List.of(
                buttonBuilder.buildButton(EmojiParser.parseToUnicode(":chart_with_upwards_trend: Ordered Statistics"),
                                          ORDERED_STATISTICS_MENU));
    }

    private List<InlineKeyboardButton> buildRemoveOrderButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":x: Remove order"),
                                                         REMOVE_ORDER.getCommandString()));
    }

    private List<InlineKeyboardButton> buildBackToOrderListButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, REMOVE_ORDER)));
    }

    private List<InlineKeyboardButton> buildOrderListAdminButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":incoming_envelope: All Order"),
                                                 GET_ALL_ORDERS),
                       buttonBuilder.buildButton(EmojiParser.parseToUnicode(":incoming_envelope: All Order By User"),
                                                 GET_ALL_ORDERS_BY_USER));
    }

    private List<InlineKeyboardButton> buildUserListAdminButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode("\uD83D\uDC68\u200D\uD83D\uDC69\u200D" +
                                                                                    "\uD83D\uDC67\u200D\uD83D\uDC66 " +
                                                                                    "Gat All users"),
                                                 GET_ALL_USERS));
    }

    private List<InlineKeyboardButton> buildProcessingOrderMenuAdminButtons() {
        return List.of(
                buttonBuilder.buildButton(EmojiParser.parseToUnicode(":hourglass_flowing_sand: Processing order"),
                                          PROCESSING_ORDERS_MENU));
    }

    private List<InlineKeyboardButton> buildGlobalStatisticsButtons() {
        return List.of(
                buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":earth_asia: Global"),
                                                  mergeBotCommand(ORDERED_STATISTICS_MENU,
                                                                  GLOBAL_ORDERED_STATISTICS_MENU)));
    }

    private List<InlineKeyboardButton> buildUserStatisticsButtons() {
        return List.of(
                buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":bust_in_silhouette: User"),
                                                  mergeBotCommand(ORDERED_STATISTICS_MENU,
                                                                  USER_ORDERED_STATISTICS_MENU)));
    }

    private List<InlineKeyboardButton> buildBackToOrderedStatisticsMenuButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, ORDERED_STATISTICS_MENU)));
    }

    private static String buildCoalOrderCommand(final Long value) {
        return ORDER.getCommandString() + ":" + COAL.getCommandString() + ":" + value;
    }

    private static String buildTobaccoOrderCommand(final TobaccoBotCommand tobaccoBotCommand, final Long value) {
        return ORDER.getCommandString() + ":" + tobaccoBotCommand.getCommandString() + ":" + value;
    }
}
