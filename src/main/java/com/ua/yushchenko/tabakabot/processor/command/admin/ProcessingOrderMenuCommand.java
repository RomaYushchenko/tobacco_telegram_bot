package com.ua.yushchenko.tabakabot.processor.command.admin;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COMPLETED_ORDER_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDERED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.PLANNED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_MENU;

import java.util.List;
import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.ui.admin.ProcessOrderMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
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
public class ProcessingOrderMenuCommand implements TobaccoCommand {

    @NonNull
    private final ProcessOrderMenuBuilder processOrderMenuBuilder;
    @NonNull
    private final OrderService orderService;

    @Override
    public BotApiMethod<?> buildMessage(final Update update, final User user) {
        log.info("execute.E: [ADMIN] Processing {} command", getCommand());

        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final Message message = callbackQuery.getMessage();
        final Long chatId = message.getChatId();
        final Integer messageId = message.getMessageId();
        final String data = callbackQuery.getData();

        final List<Object> tobaccoBotCommands = TobaccoBotCommand.getListCommandsByString(data);

        if (tobaccoBotCommands.size() == 1) {
            final var sendMessage = processOrderMenuBuilder.buildProcessingOrdersMenu(chatId, messageId);
            log.info("execute.X: [ADMIN] Processed {} command", getCommand());
            return sendMessage;
        }

        final Object secondBotCommand = tobaccoBotCommands.get(1);

        switch ((TobaccoBotCommand) secondBotCommand) {
            case PLANNED_MENU -> {
                if (tobaccoBotCommands.size() == 2) {
                    final var sendMessage = processOrderMenuBuilder.buildPlannedMenu(chatId, messageId);

                    log.info("execute.X: [ADMIN] Processed {} commands", List.of(getCommand(), PLANNED_MENU));
                    return sendMessage;
                }

                if (Objects.equals(tobaccoBotCommands.get(2), REJECT_MENU)) {
                    if (tobaccoBotCommands.size() == 4) {
                        final List<Order> ordersToRejected =
                                orderService.getPlannedOrdersByUserId((Long) tobaccoBotCommands.get(3))
                                            .stream()
                                            .map(order -> order.toBuilder()
                                                               .orderStatus(OrderStatus.REJECTED)
                                                               .build())
                                            .toList();

                        orderService.updateOrders(ordersToRejected);
                    }

                    final var sendMessage = processOrderMenuBuilder.buildRejectOrderMenu(chatId, messageId);

                    log.info("execute.X: [ADMIN] Processed {} commands",
                             List.of(getCommand(), PLANNED_MENU, REJECT_MENU));
                    return sendMessage;
                }
            }
            case ORDERED_MENU -> {
                if (tobaccoBotCommands.size() == 2) {
                    final var sendMessage = processOrderMenuBuilder.buildOrderedMenu(chatId, messageId);
                    log.info("execute.X: [ADMIN] Processed {} commands", List.of(getCommand(), ORDERED_MENU));
                    return sendMessage;
                }

                if (Objects.equals(tobaccoBotCommands.get(2), COMPLETED_ORDER_MENU)) {
                    if (tobaccoBotCommands.size() == 4) {
                        final List<Order> ordersToCompleted =
                                orderService.getOrderedOrdersByUserId((Long) tobaccoBotCommands.get(3))
                                            .stream()
                                            .map(order -> order.toBuilder()
                                                               .orderStatus(OrderStatus.COMPLETED)
                                                               .build())
                                            .toList();

                        orderService.updateOrders(ordersToCompleted);
                    }

                    final var sendMessage = processOrderMenuBuilder.buildCompletedOrderMenu(chatId, messageId);

                    log.info("execute.X: [ADMIN] Processed {} commands",
                             List.of(getCommand(), ORDERED_MENU, COMPLETED_ORDER_MENU));
                    return sendMessage;
                }
            }
        }

        log.info("execute.X: [ADMIN] Processed {} command", getCommand());
        return null;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.PROCESSING_ORDERS_MENU;
    }
}
