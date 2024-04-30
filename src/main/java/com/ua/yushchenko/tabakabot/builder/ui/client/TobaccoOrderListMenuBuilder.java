package com.ua.yushchenko.tabakabot.builder.ui.client;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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
    private final CustomButtonBuilder buttonBuilder;

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
            final var replyMarkup = InlineKeyboardMarkup.builder()
                                                        .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                        .build();

            return EditMessageText.builder()
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .text("""
                                                You don't have order items. For ordering items:\s
                                                \t 1) Click to 'back' button
                                                \t 2) Choose tobacco""")
                                  .replyMarkup(replyMarkup)
                                  .build();
        }

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buttonBuilder.buildKeyBoardToOrderListMenu())
                                                    .build();
        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("@" + userFullName + " your order items:\n" + buildTobaccoOrderListInformation(orders))
                               .replyMarkup(replyMarkup)
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

        final List<Long> tobaccoItemIds = orders.stream()
                                                .map(Order::getTobaccoItemId)
                                                .distinct()
                                                .toList();

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buttonBuilder.buildKeyBoardToRemoveOrderMenu(tobaccoItemIds))
                                                    .build();
        EditMessageText editMessageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("@" + userFullName + " your order items:\n" + buildTobaccoOrderListInformation(orders))
                               .replyMarkup(replyMarkup)
                               .build();

        log.info("buildRemoveTobaccoOrderListMenu.X: Send message is created");
        return editMessageText;
    }

    private String buildTobaccoOrderListInformation(final List<Order> userOrders) {
        final StringBuilder tobaccoOrderListInfo = new StringBuilder();

        final Map<Long, List<Order>> ordersToTobaccoId =
                userOrders.stream()
                          .collect(Collectors.groupingBy(Order::getTobaccoItemId));

        final Map<Long, Item> itemToId =
                itemService.getItemsByIds(ordersToTobaccoId.keySet())
                           .stream()
                           .collect(Collectors.toMap(Item::getItemId, Function.identity()));


        ordersToTobaccoId
                .forEach((tobaccoItemId, orders) -> {
                    final var tobaccoItem = itemToId.get(tobaccoItemId);

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
}
