package com.ua.yushchenko.tabakabot.processor.command.client;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderListMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Class that represents the processing of the {@link TobaccoCommand} {@link TobaccoBotCommand#ORDER_LIST} for Client
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListCommand implements TobaccoCommand {

    @NonNull
    private final TobaccoOrderListMenuBuilder tobaccoOrderListMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();

        final var sendMessage =
                tobaccoOrderListMenuBuilder.buildTobaccoOrderListMenu(chatId, messageId, model.getUser());
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.ORDER_LIST;
    }
}
