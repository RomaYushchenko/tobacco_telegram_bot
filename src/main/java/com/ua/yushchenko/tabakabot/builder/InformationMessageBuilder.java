package com.ua.yushchenko.tabakabot.builder;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Class that provide logic to build {@link SendMessage}
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Component
public class InformationMessageBuilder {

    /**
     * Get {@link SendMessage} with message for specific ID of chat
     *
     * @param chatId  ID of chat
     * @param message message
     * @return {@link SendMessage} with message for specific ID of chat
     */
    public SendMessage buildSendMessage(final Long chatId, final String message) {
        return SendMessage.builder()
                          .chatId(chatId)
                          .text(message)
                          .build();
    }
}
