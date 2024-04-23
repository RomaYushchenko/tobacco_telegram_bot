package com.ua.yushchenko.tabakabot.builder.ui.client;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COAL;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER_LIST;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDER_STATUS;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.SEND_ORDER_REQUEST;

import java.util.ArrayList;
import java.util.List;

import com.ua.yushchenko.tabakabot.builder.ui.InlineButtonBuilder;
import com.ua.yushchenko.tabakabot.service.TobaccoService;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Represents of builder for {@link SendMessage} based on Tobacco
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoMenuBuilder {

    @NonNull
    private final TobaccoService tobaccoService;
    @NonNull
    private final InlineButtonBuilder buttonBuilder;

    /**
     * Build {@link SendMessage} for Tobacco menu
     *
     * @param chatId ID of chat
     * @return {@link SendMessage} for Tobacco menu
     */
    public SendMessage buildTobaccoMenu(final Long chatId) {
        log.info("buildTobaccoMenu.E: Building Tobacco menu...");

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buildKeyBoardToTobaccoMenu())
                                                    .build();

        final SendMessage sendMessage = SendMessage.builder()
                                                   .chatId(chatId)
                                                   .text("Tobacco order:")
                                                   .replyMarkup(replyMarkup)
                                                   .build();

        log.info("buildTobaccoMenu.X: Send message is created");
        return sendMessage;
    }

    /**
     * Build {@link EditMessageText} for Tobacco menu
     *
     * @param chatId    ID of chat
     * @param messageId ID of edit message
     * @return {@link EditMessageText} for Tobacco menu
     */
    public EditMessageText buildBackToTobaccoMenu(final Long chatId, final Integer messageId) {
        log.info("buildBackToTobaccoMenu.E: Building back to Tobacco menu...");

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buildKeyBoardToTobaccoMenu())
                                                    .build();

        final EditMessageText sendMessage = EditMessageText.builder()
                                                           .chatId(chatId)
                                                           .messageId(messageId)
                                                           .text("Tobacco order:")
                                                           .replyMarkup(replyMarkup)
                                                           .build();

        log.info("buildBackToTobaccoMenu.X: Send message is created");
        return sendMessage;
    }

    private List<List<InlineKeyboardButton>> buildKeyBoardToTobaccoMenu() {
        final List<List<InlineKeyboardButton>> mainMenuButtons = new ArrayList<>();

        mainMenuButtons.add(buildTobaccoButtons());
        mainMenuButtons.add(buildCoalItemButtons());
        mainMenuButtons.add(buildOrderListButtons());
        mainMenuButtons.add(buildSendOrderRequestButtons());
        mainMenuButtons.add(buildOrderStatusButtons());

        return mainMenuButtons;
    }

    private List<InlineKeyboardButton> buildTobaccoButtons() {
        return tobaccoService.getAllTobacco()
                             .stream()
                             .map(tobacco -> buttonBuilder.buildButton(tobacco.getTobaccoName().getItemString(),
                                                                       tobacco.getTobaccoCommand()))
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
}
