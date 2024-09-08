package com.ua.yushchenko.tabakabot.builder.ui.client;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ua.yushchenko.tabakabot.builder.OrderedStatisticsModelBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.OrderedStatisticsModel;
import com.ua.yushchenko.tabakabot.model.domain.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Represents of builder for {@link EditMessageText} based on Ordered Statistics Menu
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class OrderedStatisticsMenuBuilder {

    @NonNull
    private final CustomButtonBuilder buttonBuilder;
    @NonNull
    private final OrderedStatisticsModelBuilder orderedStatisticsModelBuilder;

    /**
     * Build {@link EditMessageText} for Ordered Statistics Menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message
     * @return {@link EditMessageText} for Ordered Statistics Menu
     */
    public EditMessageText buildOrderedStatisticsMenu(final Long chatId, final Integer messageId) {
        log.info("buildOrderedStatisticsMenu.E: Building...");

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(buttonBuilder.buildKeyBoardToOrderedStatisticsMenu())
                                                    .build();

        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text("Ordered statistics menu")
                              .replyMarkup(replyMarkup)
                              .build();
    }

    /**
     * Build {@link EditMessageText} for Global Ordered Statistics Menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message
     * @return {@link EditMessageText} for Global Ordered Statistics Menu
     */
    public EditMessageText buildGlobalOrderedStatistics(final Long chatId, final Integer messageId) {
        log.info("buildGlobalOrderedStatistics.E: Building...");

        final var orderedStatisticsModels = orderedStatisticsModelBuilder.buildGlobal();
        final String orderedStatisticsInformation = buildOrderedStatisticsInformation(orderedStatisticsModels);

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(
                                                            buttonBuilder.buildKeyBoardToGlobalOrderedStatisticsMenu())
                                                    .build();
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text("Global ordered statistics: \n" + orderedStatisticsInformation)
                              .replyMarkup(replyMarkup)
                              .build();
    }

    /**
     * Build {@link EditMessageText} for User Ordered Statistics Menu
     *
     * @param chatId    ID of the chat
     * @param messageId ID of the message
     * @param user      instance of user
     * @return {@link EditMessageText} for User Ordered Statistics Menu
     */
    public EditMessageText buildUserOrderedStatistics(final Long chatId, final Integer messageId, final User user) {
        log.info("buildUserOrderedStatistics.E: Building for user:@{}...", user.getLinkName());

        final var orderedStatisticsModels = orderedStatisticsModelBuilder.buildUser(user);
        final String orderedStatisticsInformation = buildOrderedStatisticsInformation(orderedStatisticsModels);

        final var replyMarkup = InlineKeyboardMarkup.builder()
                                                    .keyboard(
                                                            buttonBuilder.buildKeyBoardToGlobalOrderedStatisticsMenu())
                                                    .build();
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text("Your ordered statistics: \n" + orderedStatisticsInformation)
                              .replyMarkup(replyMarkup)
                              .build();
    }

    private String buildOrderedStatisticsInformation(final List<OrderedStatisticsModel> orderedStatisticsModels) {
        final StringBuilder orderedStatisticsInfo = new StringBuilder();
        final AtomicInteger count = new AtomicInteger(1);

        orderedStatisticsModels.stream()
                               .sorted(Comparator.comparing(OrderedStatisticsModel::getCount).reversed())
                               .limit(10)
                               .forEach(orderedStatistics -> {
                                   orderedStatisticsInfo.append("\t\t\t")
                                                        .append(count.getAndIncrement())
                                                        .append(") ")
                                                        .append(orderedStatistics.getItemType().getItemString())
                                                        .append(" ")
                                                        .append(orderedStatistics.getDescription())
                                                        .append(" ")
                                                        .append(" (")
                                                        .append(orderedStatistics.getCount())
                                                        .append(")")
                                                        .append("\n");
                               });

        return orderedStatisticsInfo.toString();
    }
}
