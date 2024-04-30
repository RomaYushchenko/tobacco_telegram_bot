package com.ua.yushchenko.tabakabot.processor.command.client;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderListMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveOrderCommand implements TobaccoCommand {

    @NonNull
    private final OrderService orderService;
    @NonNull
    private final TobaccoOrderListMenuBuilder orderListMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final Update update, final User user) {
        log.info("execute.E: Processing {} command", getCommand());
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String data = callbackQuery.getData();
        final Message message = callbackQuery.getMessage();
        final Long chatId = message.getChatId();
        final Integer messageId = message.getMessageId();

        final String[] splitBotCommand = data.split(":");

        if (splitBotCommand.length > 1) {
            final Long tobaccoItemId = Long.valueOf(splitBotCommand[1]);
            orderService.removePlannedOrder(user.getUserID(), tobaccoItemId);
        }

        final var sendMessage = orderListMenuBuilder.buildRemoveTobaccoOrderListMenu(chatId, messageId, user);
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.REMOVE_ORDER;
    }
}
