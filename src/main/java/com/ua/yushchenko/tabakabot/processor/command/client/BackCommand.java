package com.ua.yushchenko.tabakabot.processor.command.client;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;

import java.util.List;
import java.util.Objects;

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

@Log4j2
@Component("backCommandOfClient")
@RequiredArgsConstructor
public class BackCommand implements TobaccoCommand {

    @NonNull
    private final TobaccoMenuBuilder tobaccoMenuBuilder;
    @NonNull
    private final TobaccoOrderListMenuBuilder orderListMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.debug("execute.E: Processing {} command", getCommand());

        final List<Object> data = model.getTobaccoBotCommands();
        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();

        final TobaccoBotCommand secondCommand = (TobaccoBotCommand) data.get(1);

        if (Objects.isNull(secondCommand)) {
            log.error("Second bot command is null");
            return null;
        }

        log.info("execute.E: Processing second {} command", secondCommand);

        switch (secondCommand) {
            case START -> {
                final var sendMessage = tobaccoMenuBuilder.buildBackToTobaccoMenu(chatId, messageId);
                log.info("execute.X: Processed second {} command", secondCommand);
                return sendMessage;
            }
            case REMOVE_ORDER -> {
                final var sendMessage =
                        orderListMenuBuilder.buildTobaccoOrderListMenu(chatId, messageId, model.getUser());
                log.info("execute.X: Processed second {} command", secondCommand);
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
