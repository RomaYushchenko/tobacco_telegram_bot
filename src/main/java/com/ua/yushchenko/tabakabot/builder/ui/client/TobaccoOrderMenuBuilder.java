package com.ua.yushchenko.tabakabot.builder.ui.client;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.mergeBotCommand;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.ua.yushchenko.tabakabot.builder.ui.InlineButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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
    private final InlineButtonBuilder buttonBuilder;

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

        final List<Item> items = itemService.getItemsByType(itemType);
        final var inlineKeyboardButtonRows = new ArrayList<>(getTobaccoItemButtonRows(items, tobaccoBotCommand));

        inlineKeyboardButtonRows.add(buildBackToStartButtons());

        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("List of " + itemType.getItemString() + ": \n\n" + getReadableAllTobaccoItems(items))
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboard(inlineKeyboardButtonRows)
                                                                .build())
                               .build();

        log.info("buildTobaccoOrderMenu.X: Send message is created");
        return messageText;
    }


    private String getReadableAllTobaccoItems(List<Item> tobaccoItemsByType) {
        return tobaccoItemsByType.stream()
                                 .map(TobaccoOrderMenuBuilder::buildTobaccoItemInfo)
                                 .collect(joining("\n"));
    }

    private List<List<InlineKeyboardButton>> getTobaccoItemButtonRows(final List<Item> tobaccoItems,
                                                                      final TobaccoBotCommand tobaccoBotCommand) {
        return Lists.partition(tobaccoItems, 6)
                    .stream()
                    .map(items -> getInlineKeyboardButtons(items, tobaccoBotCommand))
                    .toList();
    }

    private List<InlineKeyboardButton> getInlineKeyboardButtons(final List<Item> tobaccoItems,
                                                                final TobaccoBotCommand tobaccoBotCommand) {
        return tobaccoItems.stream()
                           .map(tobaccoItem ->
                                        buttonBuilder.buildButtonByString(
                                                String.valueOf(tobaccoItem.getItemId()),
                                                buildOrderCommand(tobaccoBotCommand, tobaccoItem.getItemId())))
                           .toList();
    }

    private static String buildOrderCommand(final TobaccoBotCommand tobaccoBotCommand, final Long value) {
        return ORDER.getCommandString() + ":" + tobaccoBotCommand.getCommandString() + ":" + value;
    }

    private static String buildTobaccoItemInfo(final Item item) {
        return item.getItemId() + ") " + item.getDescription() + " (" + item.getWeight() + " г.)";
    }

    private List<InlineKeyboardButton> buildBackToStartButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, START)));
    }
}
