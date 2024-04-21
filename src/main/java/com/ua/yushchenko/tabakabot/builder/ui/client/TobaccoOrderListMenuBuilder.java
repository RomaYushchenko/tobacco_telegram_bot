package com.ua.yushchenko.tabakabot.builder.ui.client;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REMOVE_ORDER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.mergeBotCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.ua.yushchenko.tabakabot.builder.ui.InlineButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.ua.yushchenko.tabakabot.service.OrderService;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Represents of builder for {@link EditMessageText} based on Tobacco Order List
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoOrderListMenuBuilder {

    @NonNull
    private final ItemService itemService;
    @NonNull
    private final OrderService orderService;
    @NonNull
    private final InlineButtonBuilder buttonBuilder;

    /**
     * Build {@link EditMessageText} for Tobacco Order List menu
     *
     * @param chatId ID of chat
     * @param user   user
     * @return {@link EditMessageText} for Tobacco Order List menu
     */
    public EditMessageText buildTobaccoOrderListMenu(final Long chatId, final Integer messageId, final User user) {
        log.info("buildTobaccoOrderListMenu.E: Building Tobacco Order List menu...");

        final List<Order> orders = orderService.getPlannedOrdersByUserId(user.getUserID());
        final String userFullName = user.getLinkName();

        if (CollectionUtils.isEmpty(orders)) {
            return EditMessageText.builder()
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .text("""
                                                You don't have order items. For ordering items:\s
                                                \t 1) Click to 'back' button
                                                \t 2) Choose tobacco""")
                                  .replyMarkup(InlineKeyboardMarkup.builder()
                                                                   .keyboardRow(buildBackToStartButtons())
                                                                   .build())
                                  .build();
        }

        final List<List<InlineKeyboardButton>> inlineKeyboardButtonRows = new ArrayList<>();
        inlineKeyboardButtonRows.add(buildRemoveOrderButtons());
        inlineKeyboardButtonRows.add(buildBackToStartButtons());

        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("@" + userFullName + " your order items:\n" + buildTobaccoOrderListInformation(orders))
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboard(inlineKeyboardButtonRows)
                                                                .build())
                               .build();

        log.info("buildTobaccoOrderListMenu.X: Send message is created");
        return messageText;
    }

    /**
     * Build {@link EditMessageText} for Tobacco Order List menu
     *
     * @param chatId    ID of chat
     * @param messageId ID of edit message
     * @param user      user
     * @return {@link EditMessageText} for Tobacco Order List menu
     */
    public EditMessageText buildRemoveTobaccoOrderListMenu(final Long chatId, final Integer messageId,
                                                           final User user) {
        log.info("buildRemoveTobaccoOrderListMenu.E: Building back to Tobacco Order List menu...");

        final List<Order> orders = orderService.getPlannedOrdersByUserId(user.getUserID());
        final String userFullName = user.getLinkName();

        final List<Long> tobaccoItemIdsWithoutDuplicates = orders.stream()
                                                                 .map(Order::getTobaccoItemId)
                                                                 .distinct()
                                                                 .toList();

        final List<List<InlineKeyboardButton>> inlineKeyboardButtonRows =
                new ArrayList<>(Lists.partition(tobaccoItemIdsWithoutDuplicates, 6)
                                     .stream()
                                     .map(this::buildRemoveTobaccoItemButtons)
                                     .toList());

        inlineKeyboardButtonRows.add(buildBackToStartButtons());

        EditMessageText editMessageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("@" + userFullName + " your order items:\n" + buildTobaccoOrderListInformation(orders))
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboard(inlineKeyboardButtonRows)
                                                                .build())
                               .build();

        log.info("buildRemoveTobaccoOrderListMenu.X: Send message is created");
        return editMessageText;
    }

    private List<InlineKeyboardButton> buildRemoveTobaccoItemButtons(final List<Long> tobaccoItemIds) {
        return tobaccoItemIds.stream()
                             .map(this::buildRemoveTobaccoItemButton)
                             .distinct()
                             .toList();
    }

    private InlineKeyboardButton buildRemoveTobaccoItemButton(final Long tobaccoItemId) {
        return buttonBuilder.buildButtonByString(String.valueOf(tobaccoItemId),
                                                 mergeBotCommand(REMOVE_ORDER, tobaccoItemId));
    }

    private String buildTobaccoOrderListInformation(final List<Order> userOrders) {
        final StringBuilder tobaccoOrderListInfo = new StringBuilder();

        userOrders.stream()
                  .collect(Collectors.groupingBy(Order::getTobaccoItemId))
                  .forEach((tobaccoItemId, orders) -> {
                      final var tobaccoItem = itemService.getItemById(tobaccoItemId);

                      tobaccoOrderListInfo.append(tobaccoItemId)
                                          .append(") ")
                                          .append(tobaccoItem.getItemType().getItemString())
                                          .append(" ")
                                          .append(tobaccoItem.getDescription());

                      final int size = orders.size();

                      if (size > 1) {
                          tobaccoOrderListInfo.append(" (x")
                                              .append(size)
                                              .append(")");
                      }

                      tobaccoOrderListInfo.append("\n");
                  });

        return tobaccoOrderListInfo.toString();
    }

    private List<InlineKeyboardButton> buildRemoveOrderButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":x: Remove order"),
                                                         mergeBotCommand(BACK, REMOVE_ORDER)));
    }

    private List<InlineKeyboardButton> buildBackToStartButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, START)));
    }
}
