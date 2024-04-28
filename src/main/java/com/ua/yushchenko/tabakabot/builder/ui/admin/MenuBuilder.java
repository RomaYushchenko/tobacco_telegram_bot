package com.ua.yushchenko.tabakabot.builder.ui.admin;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GET_ALL_ORDERS;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GET_ALL_ORDERS_BY_USER;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GET_ALL_USERS;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.PROCESSING_ORDERS_MENU;

import java.util.ArrayList;
import java.util.List;

import com.ua.yushchenko.tabakabot.builder.ui.InlineButtonBuilder;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Log4j2
@Component
@RequiredArgsConstructor
public class MenuBuilder {

    @NonNull
    private final InlineButtonBuilder buttonBuilder;

    public SendMessage buildTobaccoAdminMenu(final Long chatId) {
        return SendMessage.builder()
                          .chatId(chatId)
                          .text("Admin panel")
                          .replyMarkup(InlineKeyboardMarkup.builder()
                                                           .keyboard(buildAdminButtons())
                                                           .build())
                          .build();
    }

    public EditMessageText buildTobaccoAdminMenu(final Long chatId, final Integer messageId) {
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text("Admin panel")
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboard(buildAdminButtons())
                                                               .build())
                              .build();
    }

    private List<List<InlineKeyboardButton>> buildAdminButtons(){
        final List<List<InlineKeyboardButton>> mainMenuButtons = new ArrayList<>();

        final List<InlineKeyboardButton> ordersList =
                List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":incoming_envelope: All Order"),
                                                  GET_ALL_ORDERS),
                        buttonBuilder.buildButton(EmojiParser.parseToUnicode(":incoming_envelope: All Order By User"),
                                                  GET_ALL_ORDERS_BY_USER));

        final List<InlineKeyboardButton> userList =
                List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":incoming_envelope: Gat All users"),
                                                  GET_ALL_USERS));

        final List<InlineKeyboardButton> processingOrder =
                List.of(buttonBuilder.buildButton(EmojiParser.parseToUnicode(":hourglass_flowing_sand: Processing order"),
                                                  PROCESSING_ORDERS_MENU));

        mainMenuButtons.add(ordersList);
        mainMenuButtons.add(userList);
        mainMenuButtons.add(processingOrder);

        return mainMenuButtons;
    }
}
