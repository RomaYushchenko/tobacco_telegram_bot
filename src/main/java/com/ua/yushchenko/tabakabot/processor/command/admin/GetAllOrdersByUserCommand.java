package com.ua.yushchenko.tabakabot.processor.command.admin;

import com.ua.yushchenko.tabakabot.builder.ui.admin.OrderListBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllOrdersByUserCommand implements TobaccoCommand {

    @NonNull
    private final OrderListBuilder orderListBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: [ADMIN] Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();

        final var sendMessage = orderListBuilder.buildTobaccoAdminOrderListByUserMenu(chatId, messageId);

        log.info("execute.X: [ADMIN] Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.GET_ALL_ORDERS_BY_USER;
    }
}
