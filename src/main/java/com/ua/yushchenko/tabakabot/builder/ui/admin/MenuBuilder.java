package com.ua.yushchenko.tabakabot.builder.ui.admin;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Represents of builder for {@link SendMessage} based on Main Admin Tobacco menu
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class MenuBuilder {

    @NonNull
    private final CustomButtonBuilder buttonBuilder;

    /**
     * Build {@link SendMessage} main Admin menu
     *
     * @param chatId ID of chat
     * @return {@link SendMessage} main Admin menu
     */
    public SendMessage buildTobaccoAdminMenu(final Long chatId) {
        return SendMessage.builder()
                          .chatId(chatId)
                          .text("Admin panel")
                          .replyMarkup(InlineKeyboardMarkup.builder()
                                                           .keyboard(buttonBuilder.buildKeyBoardToAdminTobaccoMenu())
                                                           .build())
                          .build();
    }

    /**
     * Build {@link EditMessageText} main Admin menu for current chat and message
     *
     * @param chatId    ID of chat
     * @param messageId ID of message
     * @return {@link EditMessageText} main Admin menu for current chat and message
     */
    public EditMessageText buildTobaccoAdminMenu(final Long chatId, final Integer messageId) {
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text("Admin panel")
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(
                                                                       buttonBuilder.buildKeyBoardToAdminTobaccoMenu())
                                                               .build())
                              .build();
    }
}
