package com.ua.yushchenko.tabakabot.processor;

import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.getFirstCommandOfMessageEntityBotCommand;

import java.util.List;
import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.InformationMessageBuilder;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.model.mapper.UserMapper;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.processor.command.admin.AdminCommandFactory;
import com.ua.yushchenko.tabakabot.service.UserService;
import com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoAdminBotProcessor extends TelegramWebhookBot {

    @Value("${telegram.tobacco.admin.bot.token}")
    private String tobaccoAdminBotToken;

    @NonNull
    private final InformationMessageBuilder informationMessageBuilder;
    @NonNull
    private final UserMapper userMapper;
    @NonNull
    private final UserService userService;
    @NonNull
    private final AdminCommandFactory adminCommandFactory;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(final Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                final Message message = update.getMessage();
                final Long chatId = update.getMessage().getChatId();

                if (TobaccoBotCommandUtility.isTobaccoBotCommand(message)) {
                    final var botCommand = getFirstCommandOfMessageEntityBotCommand(message);
                    log.info("onWebhookUpdateReceived.E: [ADMIN] Start processing {} command", botCommand);

                    final User user = userMapper.apiToDomain(message.getFrom());

                    final var sendMessage = adminCommandFactory.retrieveCommand(List.of(botCommand))
                                                               .buildMessage(update, user);

                    if (Objects.nonNull(sendMessage)) {
                        log.info("onWebhookUpdateReceived.X: [ADMIN] Sending message to client");
                        execute(sendMessage);
                    }
                } else {
                    execute(informationMessageBuilder.buildSendMessage(chatId, "It is not command"));
                }

                return null;
            } else if (update.hasCallbackQuery()) {
                log.info("onWebhookUpdateReceived.E: [ADMIN] Starting processing callback query");
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final Message message = callbackQuery.getMessage();
                final Long chatId = message.getChatId();
                final Long userId = callbackQuery.getFrom().getId();
                final String data = callbackQuery.getData();

                final User user = userService.getUserById(userId);

                if (Objects.isNull(user)) {
                    String userName = callbackQuery.getFrom().getUserName();

                    final String text = "User " + userName + " hasn't registered!\nPlease log in!";

                    execute(informationMessageBuilder.buildSendMessage(chatId, text));
                    log.error("User {} hasn't registered", userName);
                    return null;
                }

                final List<Object> tobaccoBotCommands = TobaccoBotCommand.getListCommandsByString(data);

                if (CollectionUtils.isEmpty(tobaccoBotCommands)) {
                    execute(informationMessageBuilder.buildSendMessage(chatId, "Unhandled callback command!!!"));
                    return null;
                }

                final TobaccoCommand tobaccoCommand = adminCommandFactory.retrieveCommand(tobaccoBotCommands);

                if (tobaccoCommand == null) {
                    log.warn("onWebhookUpdateReceived.X: [ADMIN] Don't found bot command");
                    return null;
                }

                final var sendMessage = tobaccoCommand.buildMessage(update, user);

                if (Objects.nonNull(sendMessage)) {
                    log.info("onWebhookUpdateReceived.X: [ADMIN] Sending message to client");
                    execute(sendMessage);
                }

                return null;
            }
        } catch (TelegramApiException e) {
            log.error("Unhandled error: ", e);
            return null;
        }

        return null;
    }

    @Override
    public String getBotUsername() {
        return "Tobacco Bot Admin";
    }

    @Override
    public String getBotPath() {
        return "/admin/update";
    }

    @Override
    public String getBotToken() {
        return tobaccoAdminBotToken;
    }
}
