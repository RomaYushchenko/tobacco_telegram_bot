package com.ua.yushchenko.tabakabot.builder.ui;

import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Represents of builder for {@link InlineKeyboardButton} based on bot command
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Component
public class InlineButtonBuilder {

    /**
     * Build {@link InlineKeyboardButton}
     *
     * @param text       message of button
     * @param botCommand call back Data
     * @return {@link InlineKeyboardButton}
     */
    public InlineKeyboardButton buildButton(final String text, final TobaccoBotCommand botCommand) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(botCommand.getCommandString())
                                   .build();
    }

    /**
     * Build {@link InlineKeyboardButton}
     *
     * @param text       message of button
     * @param botCommand call back Data
     * @return {@link InlineKeyboardButton}
     */
    public InlineKeyboardButton buildButtonByString(final String text, final String botCommand) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(botCommand)
                                   .build();
    }
}
