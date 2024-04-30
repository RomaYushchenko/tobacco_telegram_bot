package com.ua.yushchenko.tabakabot.processor.command.client;

import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCommand implements TobaccoCommand {

    @NonNull
    private final OrderService orderService;

    @Override
    public BotApiMethod<?> buildMessage(final Update update, final User user) {
        log.info("execute.E: Processing {} command", getCommand());
        final String data = update.getCallbackQuery().getData();

        final String[] splitCommands = data.split(":");
        final long itemId = Long.parseLong(splitCommands[2]);

        orderService.addOrderToUser(user.getUserID(), itemId);

        log.info("execute.X: Processed {} command", getCommand());
        return null;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.ORDER;
    }
}
