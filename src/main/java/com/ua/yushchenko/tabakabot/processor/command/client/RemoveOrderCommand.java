package com.ua.yushchenko.tabakabot.processor.command.client;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderListMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveOrderCommand implements TobaccoCommand {

    @NonNull
    private final OrderService orderService;
    @NonNull
    private final TobaccoOrderListMenuBuilder orderListMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();

        if (model.getTobaccoBotCommands().size() > 1) {
            final Long tobaccoItemId = (Long) model.getTobaccoBotCommands().get(1);

            orderService.removePlannedOrder(model.getUser().getUserID(), tobaccoItemId);
        }

        final var sendMessage =
                orderListMenuBuilder.buildRemoveTobaccoOrderListMenu(chatId, messageId, model.getUser());
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.REMOVE_ORDER;
    }
}
