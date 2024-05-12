package com.ua.yushchenko.tabakabot.builder.ui.client;

import java.util.List;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.service.TobaccoService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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
    private final CustomButtonBuilder customButtonBuilder;
    @NonNull
    private final TobaccoService tobaccoService;

    /**
     * Build {@link SendMessage} for Tobacco menu
     *
     * @param chatId ID of chat
     * @return {@link SendMessage} for Tobacco menu
     */
    public SendMessage buildTobaccoMenu(final Long chatId) {
        log.info("buildTobaccoMenu.E: Building Tobacco menu...");

        final List<Tobacco> tobaccos = tobaccoService.getAllTobacco();

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(customButtonBuilder.buildKeyBoardToTobaccoMenu(tobaccos))
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

        final List<Tobacco> tobaccos = tobaccoService.getAllTobacco();
        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(customButtonBuilder.buildKeyBoardToTobaccoMenu(tobaccos))
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
}
