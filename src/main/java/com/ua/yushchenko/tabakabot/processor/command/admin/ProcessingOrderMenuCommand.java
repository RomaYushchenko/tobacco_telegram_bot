package com.ua.yushchenko.tabakabot.processor.command.admin;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.COMPLETED_ORDER_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.ORDERED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.PLANNED_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_MENU;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_ORDERED_ITEM;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.REJECT_ORDERED_MENU_ITEM;

import java.util.List;
import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.ui.admin.ProcessOrderMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.service.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Class that represents the processing of the {@link TobaccoCommand}
 * {@link TobaccoBotCommand#PROCESSING_ORDERS_MENU} for Admin
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessingOrderMenuCommand implements TobaccoCommand {

    @NonNull
    private final ProcessOrderMenuBuilder processOrderMenuBuilder;
    @NonNull
    private final OrderService orderService;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: [ADMIN] Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();
        final List<Object> tobaccoBotCommands = model.getTobaccoBotCommands();

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

                    final var sendMessage = processOrderMenuBuilder.buildRejectPlannedOrderMenu(chatId, messageId);

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

                    final var sendMessage = processOrderMenuBuilder.buildCompletedOrderedOrderMenu(chatId, messageId);

                    log.info("execute.X: [ADMIN] Processed {} commands",
                             List.of(getCommand(), ORDERED_MENU, COMPLETED_ORDER_MENU));
                    return sendMessage;

                } else if (Objects.equals(tobaccoBotCommands.get(2), REJECT_ORDERED_MENU_ITEM)) {
                    if (tobaccoBotCommands.size() == 4) {
                        final Long userId = (Long) tobaccoBotCommands.get(3);
                        final var sendMessage = processOrderMenuBuilder.buildListOfOrderByUserMenu(chatId, messageId, userId);
                        log.info("execute.X: [ADMIN] Processed {} commands", List.of(getCommand(), ORDERED_MENU, REJECT_ORDERED_MENU_ITEM));
                        return sendMessage;
                    }

                    final var sendMessage = processOrderMenuBuilder.buildRejectedOrderedOrderMenu(chatId, messageId);

                    log.info("execute.X: [ADMIN] Processed {} commands",
                             List.of(getCommand(), ORDERED_MENU, REJECT_ORDERED_MENU_ITEM));

                    return sendMessage;

                } else if (Objects.equals(tobaccoBotCommands.get(2), REJECT_ORDERED_ITEM)) {
                    if (tobaccoBotCommands.size() == 5) {
                        final Long userId = (Long) tobaccoBotCommands.get(3);
                        final Long itemId = (Long) tobaccoBotCommands.get(4);

                        final Order order = orderService.getFirstOrderedOrderByUserIdAndItemId(userId, itemId);

                        if (Objects.nonNull(order)) {
                            Order rejectedOrder = order.toBuilder()
                                                       .orderStatus(OrderStatus.REJECTED)
                                                       .build();

                            orderService.updateOrders(List.of(rejectedOrder));
                        }

                        final var sendMessage = processOrderMenuBuilder.buildListOfOrderByUserMenu(chatId, messageId, userId);
                        log.info("execute.X: [ADMIN] Processed {} commands", List.of(getCommand(), ORDERED_MENU, REJECT_ORDERED_ITEM));
                        return sendMessage;
                    }
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
