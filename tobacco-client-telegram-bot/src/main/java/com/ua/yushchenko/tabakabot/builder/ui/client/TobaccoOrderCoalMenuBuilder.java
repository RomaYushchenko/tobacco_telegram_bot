package com.ua.yushchenko.tabakabot.builder.ui.client;

import static com.ua.yushchenko.tabakabot.model.enums.ItemType.COAL;

import java.util.List;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.service.ItemService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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
    private final CustomButtonBuilder buttonBuilder;

    /**
     * Build {@link EditMessageText} for Tobacco Order Coal menu
     *
     * @param chatId    ID of chat
     * @param messageId ID of edit message
     * @return {@link EditMessageText} for Tobacco Order Coal menu
     */
    public EditMessageText buildTobaccoOrderCoalMenu(final Long chatId, final Integer messageId) {
        log.info("buildTobaccoOrderCoalMenu.E: Building Tobacco Order Coal menu...");

        final List<Item> coals = itemService.getAvailableItemsByType(COAL);

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buttonBuilder.buildKeyBoardToOrderCoalMenu(coals))
                                                    .build();

        final EditMessageText messageText =
                EditMessageText.builder()
                               .chatId(chatId)
                               .messageId(messageId)
                               .text("List of " + COAL.getItemString() + ":")
                               .replyMarkup(replyMarkup)
                               .build();

        log.info("buildTobaccoOrderCoalMenu.X: Send message is created");
        return messageText;
    }
}
