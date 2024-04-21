package com.ua.yushchenko.tabakabot.processor;

import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_CLASSIC;
import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_LIGHT;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BOT_COMMAND;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.TABAKA_420_LIGHT;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.getEnumByString;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ua.yushchenko.tabakabot.builder.InformationMessageBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoMenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderCoalMenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderListMenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderMenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoSendOrderRequestMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.model.mapper.UserMapper;
import com.ua.yushchenko.tabakabot.service.OrderService;
import com.ua.yushchenko.tabakabot.service.UserService;
import com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Class that provide main Tobacco Bot functionality
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@RequiredArgsConstructor
public class TobaccoClientBotProcessor extends TelegramWebhookBot {

    private final UserMapper userMapper;
    private final UserService userService;
    private final OrderService orderService;
    private final InformationMessageBuilder informationMessageBuilder;
    private final TobaccoMenuBuilder tobaccoMenuBuilder;
    private final TobaccoOrderMenuBuilder tobaccoOrderMenuBuilder;
    private final TobaccoOrderListMenuBuilder tobaccoOrderListMenuBuilder;
    private final TobaccoSendOrderRequestMenuBuilder tobaccoSendOrderRequestMenuBuilder;
    private final TobaccoOrderCoalMenuBuilder tobaccoOrderCoalMenuBuilder;

    @Getter
    private final String botToken;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(final Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                final Message message = update.getMessage();
                final Long chatId = update.getMessage().getChatId();

                if (TobaccoBotCommandUtility.isTobaccoBotCommand(message)) {
                    final String botCommand = getFirstTextOfMessageEntityBotCommand(message);
                    log.info("onUpdateReceived.E: Start processing {} command", botCommand);

                    if (Objects.equals(botCommand, START.getCommandString())) {
                        final User user = userMapper.apiToDomain(message.getFrom());
                        userService.saveUser(user);

                        execute(tobaccoMenuBuilder.buildTobaccoMenu(chatId));

                        log.info("onUpdateReceived.X: Finish processing {} command", botCommand);
                    } else {
                        execute(informationMessageBuilder.buildSendMessage(chatId, "Command is not support"));
                        log.info("onUpdateReceived.X: Command {} is not support", botCommand);
                    }
                } else {
                    execute(informationMessageBuilder.buildSendMessage(chatId, "It is not command"));
                }
            } else if (update.hasCallbackQuery()) {
                log.info("onUpdateReceived.E: Starting processing callback query");
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final Long chatId = callbackQuery.getMessage().getChatId();
                final Integer messageId = callbackQuery.getMessage().getMessageId();
                final Long userId = callbackQuery.getFrom().getId();
                final String data = callbackQuery.getData();

                final User user = userService.getUserById(userId);

                if (Objects.isNull(user)) {
                    String userName = callbackQuery.getFrom().getUserName();

                    final String message = "User " + userName + " hasn't registered!\nPlease log in!";

                    execute(informationMessageBuilder.buildSendMessage(chatId, message));
                    log.error("User {} hasn't registered", userName);
                    return null;
                }

                final TobaccoBotCommand tobaccoBotCommand = getFirstTobaccoBotCommand(data);

                if (Objects.isNull(tobaccoBotCommand)) {
                    execute(informationMessageBuilder.buildSendMessage(chatId, "Unhandled callback command!!!"));
                    log.error("@{} tried to use unhandled callback command {}", user.getLinkName(), data);
                    return null;
                }

                switch (tobaccoBotCommand) {
                    case TABAKA_420_LIGHT -> {
                        execute(tobaccoOrderMenuBuilder.buildTobaccoOrderMenu(chatId, messageId, TOBACCO_420_LIGHT,
                                                                              tobaccoBotCommand));
                        log.info("onUpdateReceived.X: Finish processing {} command", tobaccoBotCommand);
                    }
                    case TABAKA_420_CLASSIC -> {
                        execute(tobaccoOrderMenuBuilder.buildTobaccoOrderMenu(chatId, messageId, TOBACCO_420_CLASSIC,
                                                                              tobaccoBotCommand));
                        log.info("onUpdateReceived.X: Finish processing {} command", tobaccoBotCommand);
                    }
                    case COAL -> {
                        execute(tobaccoOrderCoalMenuBuilder.buildTobaccoOrderCoalMenu(chatId, messageId));
                        log.info("onUpdateReceived.X: Finish processing {} command", tobaccoBotCommand);
                    }
                    case ORDER_LIST -> {
                        execute(tobaccoOrderListMenuBuilder.buildTobaccoOrderListMenu(chatId, messageId, user));
                        log.info("onUpdateReceived.X: Finish processing {} command", tobaccoBotCommand);
                    }
                    case ORDER -> {
                        final String[] splitCommands = data.split(":");
                        final Long itemId = Long.parseLong(splitCommands[2]);

                        orderService.addOrderToUser(userId, itemId);

                        log.info("onUpdateReceived.X: Finish processing {} command", tobaccoBotCommand);
                    }
                    case SEND_ORDER_REQUEST -> {
                        final List<Order> ordersByUserId = orderService.getOrdersByUserId(user.getUserID())
                                                                       .stream()
                                                                       .map(order -> order.toBuilder()
                                                                                          .orderStatus(OrderStatus.ORDERED)
                                                                                          .build())
                                                                       .toList();

                        orderService.updateOrders(ordersByUserId);

                        execute(tobaccoSendOrderRequestMenuBuilder.buildSendOrderRequestMenu(chatId, messageId, user));
                        log.info("onUpdateReceived.X: Finish processing {} command", tobaccoBotCommand);
                    }
                    case REMOVE_ORDER -> {
                        final String[] splitBotCommand = data.split(":");
                        final Long tobaccoItemId = Long.valueOf(splitBotCommand[1]);

                        orderService.removeOrder(userId, tobaccoItemId);

                        execute(tobaccoOrderListMenuBuilder.buildRemoveTobaccoOrderListMenu(chatId, messageId, user));
                    }
                    case BACK -> {
                        final String[] splitBotCommand = data.split(":");
                        final TobaccoBotCommand secondCommand = getEnumByString(splitBotCommand[1]);

                        if (Objects.isNull(secondCommand)) {
                            log.error("Second bot command is null");
                            return null;
                        }

                        switch (secondCommand) {
                            case START -> execute(tobaccoMenuBuilder.buildBackToTobaccoMenu(chatId, messageId));
                            case REMOVE_ORDER ->
                                    execute(tobaccoOrderListMenuBuilder.buildRemoveTobaccoOrderListMenu(chatId,
                                                                                                        messageId,
                                                                                                        user));
                            default -> log.error("Unhandled second command: {}", secondCommand);
                        }
                    }
                    default -> log.error("Unhandled callback command: {}", data);
                }
            } else {
                log.error("Unchecked message!!!");
            }
        } catch (final TelegramApiException e) {
            log.error("Unhandled error: ", e);
            return null;
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return "/client/update";
    }

    private static String getFirstTextOfMessageEntityBotCommand(final Message message) {
        final String botCommand =
                Optional.ofNullable(TobaccoBotCommandUtility.findFirstMessageEntityByCommandType(message, BOT_COMMAND))
                        .map(MessageEntity::getText)
                        .orElse(null);

        final String[] splitBotCommand = botCommand.split("/");
        return splitBotCommand[1];
    }

    private static TobaccoBotCommand getFirstTobaccoBotCommand(final String data) {
        if (data.contains(":")) {
            final String[] splitCommands = data.split(":");
            return TobaccoBotCommand.getEnumByString(splitCommands[0]);
        }

        return TobaccoBotCommand.getEnumByString(data);
    }

    @Override
    public String getBotUsername() {
        return "Tabaka Bot";
    }
}
