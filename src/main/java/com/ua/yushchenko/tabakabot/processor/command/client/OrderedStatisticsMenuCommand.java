package com.ua.yushchenko.tabakabot.processor.command.client;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.GLOBAL_ORDERED_STATISTICS_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.USER_ORDERED_STATISTICS_MENU;

import java.util.List;

import com.ua.yushchenko.tabakabot.builder.ui.client.OrderedStatisticsMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Class that represents the processing of the {@link TobaccoCommand}
 * {@link TobaccoBotCommand#ORDERED_STATISTICS_MENU} for Client
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderedStatisticsMenuCommand implements TobaccoCommand {

    @NonNull
    private final OrderedStatisticsMenuBuilder orderedStatisticsMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("buildMessage.E: [CLIENT] Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();
        final List<Object> tobaccoBotCommands = model.getTobaccoBotCommands();

        if (tobaccoBotCommands.size() == 1) {
            final var sendMessage = orderedStatisticsMenuBuilder.buildOrderedStatisticsMenu(chatId, messageId);
            log.info("buildMessage.X: [CLIENT] Processed {} command", getCommand());
            return sendMessage;
        }

        final Object secondBotCommand = tobaccoBotCommands.get(1);

        switch ((TobaccoBotCommand) secondBotCommand) {
            case GLOBAL_ORDERED_STATISTICS_MENU -> {
                final var sendMessage = orderedStatisticsMenuBuilder.buildGlobalOrderedStatistics(chatId, messageId);
                log.info("buildMessage.X: [CLIENT] Processed {} commands", List.of(getCommand(),
                                                                                   GLOBAL_ORDERED_STATISTICS_MENU));
                return sendMessage;
            }
            case USER_ORDERED_STATISTICS_MENU -> {
                final var sendMessage = orderedStatisticsMenuBuilder.buildUserOrderedStatistics(chatId, messageId, model.getUser());
                log.info("buildMessage.X: [CLIENT] Processed {} commands", List.of(getCommand(),
                                                                                   USER_ORDERED_STATISTICS_MENU));
                return sendMessage;
            }
        }

        log.info("buildMessage.X: [CLIENT] Processed {} command", getCommand());
        return null;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.ORDERED_STATISTICS_MENU;
    }
}
