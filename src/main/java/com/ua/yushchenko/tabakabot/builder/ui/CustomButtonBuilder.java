package com.ua.yushchenko.tabakabot.builder.ui;


import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COAL;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER_LIST;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER_STATUS;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REMOVE_ORDER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.SEND_ORDER_REQUEST;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.mergeBotCommand;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.ua.yushchenko.tabakabot.model.domain.Item;
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
     * Build list of {@link InlineKeyboardButton} for Back to Start menu
     *
     * @return list of {@link InlineKeyboardButton} for Back to Start menu
     */
    public List<InlineKeyboardButton> buildBackToStartButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, START)));
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

    private List<InlineKeyboardButton> buildCoalItemButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode("\uD83E\uDEA8 Coal"), COAL));
    }

    private List<InlineKeyboardButton> buildOrderListButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":scroll: Order list"), ORDER_LIST));
    }

    private List<InlineKeyboardButton> buildSendOrderRequestButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":incoming_envelope: Send Order"),
                                                 SEND_ORDER_REQUEST));
    }

    private List<InlineKeyboardButton> buildOrderStatusButtons() {
        return List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":hourglass_flowing_sand: Order Status"),
                                                 ORDER_STATUS));
    }

    private List<InlineKeyboardButton> buildRemoveOrderButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":x: Remove order"),
                                                         REMOVE_ORDER.getCommandString()));
    }

    private List<InlineKeyboardButton> buildBackToOrderListButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, REMOVE_ORDER)));
    }

    private static String buildCoalOrderCommand(final Long value) {
        return ORDER.getCommandString() + ":" + COAL.getCommandString() + ":" + value;
    }

    private static String buildTobaccoOrderCommand(final TobaccoBotCommand tobaccoBotCommand, final Long value) {
        return ORDER.getCommandString() + ":" + tobaccoBotCommand.getCommandString() + ":" + value;
    }
}
