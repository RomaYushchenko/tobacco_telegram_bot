package com.ua.yushchenko.tabakabot.processor.command.client;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;

import java.util.List;
import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.ui.client.OrderedStatisticsMenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoMenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderListMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Class that represents the processing of the {@link TobaccoCommand} {@link TobaccoBotCommand#BACK} for Client
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component("backCommandOfClient")
@RequiredArgsConstructor
public class BackCommand implements TobaccoCommand {

    @NonNull
    private final TobaccoMenuBuilder tobaccoMenuBuilder;
    @NonNull
    private final TobaccoOrderListMenuBuilder orderListMenuBuilder;
    @NonNull
    private final OrderedStatisticsMenuBuilder orderedStatisticsMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.debug("buildMessage.E: Processing {} command", getCommand());

        final List<Object> data = model.getTobaccoBotCommands();
        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();

        final TobaccoBotCommand secondCommand = (TobaccoBotCommand) data.get(1);

        if (Objects.isNull(secondCommand)) {
            log.error("Second bot command is null");
            return null;
        }

        log.info("buildMessage.E: Processing second {} command", secondCommand);

        switch (secondCommand) {
            case START -> {
                final var sendMessage = tobaccoMenuBuilder.buildBackToTobaccoMenu(chatId, messageId);
                log.info("buildMessage.X: Processed second {} command", secondCommand);
                return sendMessage;
            }
            case REMOVE_ORDER -> {
                final var sendMessage =
                        orderListMenuBuilder.buildTobaccoOrderListMenu(chatId, messageId, model.getUser());
                log.info("buildMessage.X: Processed second {} command", secondCommand);
                return sendMessage;
            }
            case ORDERED_STATISTICS_MENU -> {
                final var sendMessage =
                        orderedStatisticsMenuBuilder.buildOrderedStatisticsMenu(chatId, messageId);
                log.info("buildMessage.X: Processed second {} command", secondCommand);
                return sendMessage;
            }
            default -> {
                log.error("Unhandled second command: {}", secondCommand);
                return null;
            }
        }
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return BACK;
    }
}
