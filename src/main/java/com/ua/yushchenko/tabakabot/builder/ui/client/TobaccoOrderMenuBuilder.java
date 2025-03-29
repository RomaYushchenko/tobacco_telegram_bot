package com.ua.yushchenko.tabakabot.builder.ui.client;

import static java.util.stream.Collectors.joining;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.service.ItemService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Represents of builder for {@link EditMessageText} based on Tobacco Order
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoOrderMenuBuilder {

    @NonNull
    private final ItemService itemService;
    @NonNull
    private final CustomButtonBuilder buttonBuilder;

    /**
     * Build {@link EditMessageText} for Tobacco Order menu
     *
     * @param chatId   ID of chat
     * @param itemType type of item
     * @return {@link EditMessageText} for Tobacco Order menu
     */
    public EditMessageText buildTobaccoOrderMenu(final Long chatId, final Integer messageId,
                                                 final ItemType itemType,
                                                 final TobaccoBotCommand tobaccoBotCommand) {
        log.info("buildTobaccoOrderMenu.E: Building Tobacco Order menu...");

        final List<Item> items = itemService.getAvailableItemsByType(itemType)
                                            .stream()
                                            .sorted(Comparator.comparing(Item::getItemId))
                                            .collect(Collectors.toList());

        final List<Long> itemIds = items.stream()
                                        .map(Item::getItemId)
                                        .collect(Collectors.toList());

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buttonBuilder.buildKeyBoardToOrderTobaccoMenu(itemIds,
                                                                                                            tobaccoBotCommand))
                                                    .build();
        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("List of " + itemType.getItemString() + ": \n\n" + getReadableAllTobaccoItems(items))
                               .replyMarkup(replyMarkup)
                               .build();

        log.info("buildTobaccoOrderMenu.X: Send message is created");
        return messageText;
    }


    private String getReadableAllTobaccoItems(final List<Item> tobaccoItemsByType) {
        final AtomicInteger counter = new AtomicInteger(1);

        return tobaccoItemsByType.stream()
                                 .map(item ->  buildTobaccoItemInfo(counter.getAndIncrement(), item))
                                 .collect(joining("\n"));
    }

    private static String buildTobaccoItemInfo(final int counter, final Item item) {
        return counter + ") " + item.getDescription() + " (" + item.getWeight() + " г.)";
    }
}
