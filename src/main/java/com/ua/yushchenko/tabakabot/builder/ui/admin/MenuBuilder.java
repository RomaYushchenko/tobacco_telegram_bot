package com.ua.yushchenko.tabakabot.builder.ui.admin;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
@RequiredArgsConstructor
public class MenuBuilder {

    @NonNull
    private final CustomButtonBuilder buttonBuilder;

    public SendMessage buildTobaccoAdminMenu(final Long chatId) {
        return SendMessage.builder()
                          .chatId(chatId)
                          .text("Admin panel")
                          .replyMarkup(InlineKeyboardMarkup.builder()
                                                           .keyboard(buttonBuilder.buildKeyBoardToAdminTobaccoMenu())
                                                           .build())
                          .build();
    }

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
