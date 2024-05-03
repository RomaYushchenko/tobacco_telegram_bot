package com.ua.yushchenko.tabakabot.processor.command.client;

import static com.ua.yushchenko.tabakabot.model.enums.OrderStatus.PLANNED;

import java.util.List;
import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoSendOrderRequestMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendRequestOrderCommand implements TobaccoCommand {

    @NonNull
    private final OrderService orderService;
    @NonNull
    private final TobaccoSendOrderRequestMenuBuilder orderRequestMenuBuilder;


    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();
        final User user = model.getUser();

        final List<Order> ordersByUserId =
                orderService.getOrdersByUserId(user.getUserID())
                            .stream()
                            .filter(order -> Objects.equals(order.getOrderStatus(), PLANNED))
                            .map(order -> order.toBuilder()
                                               .orderStatus(OrderStatus.ORDERED)
                                               .build())
                            .toList();

        if (!CollectionUtils.isEmpty(ordersByUserId)) {
            orderService.updateOrders(ordersByUserId);
        }


        final var sendMessage = orderRequestMenuBuilder.buildSendOrderRequestMenu(chatId, messageId, user);
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.SEND_ORDER_REQUEST;
    }
}
