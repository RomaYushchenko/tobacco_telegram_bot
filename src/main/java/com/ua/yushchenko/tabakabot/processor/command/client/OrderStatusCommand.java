package com.ua.yushchenko.tabakabot.processor.command.client;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderStatusMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusCommand implements TobaccoCommand {

    @NonNull
    private final TobaccoOrderStatusMenuBuilder orderStatusMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final Update update, final User user) {
        log.info("execute.E: Processing {} command", getCommand());
        final Message message = update.getCallbackQuery().getMessage();
        final Long chatId = message.getChatId();
        final Integer messageId = message.getMessageId();

        final var sendMessage = orderStatusMenuBuilder.buildOrderStatusMenu(chatId, messageId, user);
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.ORDER_STATUS;
    }
}
