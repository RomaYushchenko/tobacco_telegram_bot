package com.ua.yushchenko.tabakabot.processor;

import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.getFirstCommandOfMessageEntityBotCommand;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.getFirstTobaccoBotCommand;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.isTobaccoBotCommand;

import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.InformationMessageBuilder;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.model.mapper.UserMapper;
import com.ua.yushchenko.tabakabot.processor.command.client.ClientCommandFactory;
import com.ua.yushchenko.tabakabot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Class that provide main Tobacco Bot functionality
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoClientBotProcessor extends TelegramWebhookBot {

    @Value("${telegram.tobacco.clint.bot.token}")
    private String tobaccoClientBotToken;

    @NonNull
    private final UserMapper userMapper;
    @NonNull
    private final UserService userService;
    @NonNull
    private final InformationMessageBuilder informationMessageBuilder;
    @NonNull
    private final ClientCommandFactory clientCommandFactory;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(final Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                final Message message = update.getMessage();
                final Long chatId = update.getMessage().getChatId();

                if (isTobaccoBotCommand(message)) {
                    final var botCommand = getFirstCommandOfMessageEntityBotCommand(message);

                    final User user = userMapper.apiToDomain(message.getFrom());
                    userService.saveUser(user);

                    final var sendMessage = clientCommandFactory.retrieveCommand(botCommand)
                                                                .buildMessage(update, user);

                    if (Objects.nonNull(sendMessage)) {
                        log.info("onWebhookUpdateReceived.X: Sending message to client");
                        execute(sendMessage);
                    }

                    return null;
                } else {
                    execute(informationMessageBuilder.buildSendMessage(chatId, "It is not command"));
                }
            } else if (update.hasCallbackQuery()) {
                log.info("onWebhookUpdateReceived.E: Starting processing callback query");
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final Long chatId = callbackQuery.getMessage().getChatId();
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

                final var sendMessage = clientCommandFactory.retrieveCommand(tobaccoBotCommand)
                                                            .buildMessage(update, user);

                if (Objects.nonNull(sendMessage)) {
                    log.info("onWebhookUpdateReceived.X: Sending message to client");
                    execute(sendMessage);
                }

                return null;
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

    @Override
    public String getBotUsername() {
        return "Tabaka Bot";
    }

    @Override
    public String getBotToken() {
        return tobaccoClientBotToken;
    }
}
