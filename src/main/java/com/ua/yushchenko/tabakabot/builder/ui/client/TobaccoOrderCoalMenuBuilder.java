package com.ua.yushchenko.tabakabot.builder.ui.client;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COAL;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER_420_LIGHT;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.mergeBotCommand;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.ua.yushchenko.tabakabot.builder.ui.InlineButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Represents of builder for {@link EditMessageText} based on Tobacco Order Coal
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoOrderCoalMenuBuilder {

    @NonNull
    private final ItemService itemService;
    @NonNull
    private final InlineButtonBuilder buttonBuilder;

    public EditMessageText buildTobaccoOrderCoalMenu(final Long chatId, final Integer messageId) {
        log.info("buildTobaccoOrderCoalMenu.E: Building Tobacco Order Coal menu...");

        final List<Item> coals = itemService.getItemsByType(ItemType.COAL);
        final var inlineKeyboardButtonRows = new ArrayList<>(getCoalItemButtonRows(coals));

        inlineKeyboardButtonRows.add(buildBackToStartButtons());

        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("List of " + ItemType.COAL.getItemString() + ":")
                               .replyMarkup(InlineKeyboardMarkup.builder()
                                                                .keyboard(inlineKeyboardButtonRows)
                                                                .build())
                               .build();

        log.info("buildTobaccoOrderCoalMenu.X: Send message is created");
        return messageText;
    }

    private List<List<InlineKeyboardButton>> getCoalItemButtonRows(final List<Item> coals) {
        return Lists.partition(coals, 6)
                    .stream()
                    .map(this::getInlineKeyboardButtons)
                    .toList();
    }

    private List<InlineKeyboardButton> getInlineKeyboardButtons(final List<Item> coals) {
        return coals.stream()
                    .map(coal -> buttonBuilder.buildButtonByString(String.valueOf(coal.getDescription()),
                                                                   buildOrderCommand(coal.getItemId())))
                    .toList();
    }

    private static String buildOrderCommand(final Long value) {
        return ORDER.getCommandString() + ":" + COAL.getCommandString() + ":" + value;
    }

    private List<InlineKeyboardButton> buildBackToStartButtons() {
        return List.of(buttonBuilder.buildButtonByString(EmojiParser.parseToUnicode(":arrow_left: Back"),
                                                         mergeBotCommand(BACK, START)));
    }
}
