package com.ua.yushchenko.tabakabot.processor;

import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.UserRequestModelBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.processor.command.client.ClientCommandFactory;
import com.ua.yushchenko.tabakabot.processor.common.UserProcessor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Class that represents the processing Telegram Bot requests
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
    private final ClientCommandFactory clientCommandFactory;
    @NonNull
    private final UserRequestModelBuilder userRequestModelBuilder;
    @NonNull
    private final UserProcessor userProcessor;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(final Update update) {
        try {
            final UserRequestModel model = userRequestModelBuilder.build(update);

            userProcessor.preProcessingUser(model);

            final TobaccoCommand tobaccoCommand = clientCommandFactory.retrieveCommand(model.getTobaccoBotCommands());

            if (tobaccoCommand == null) {
                log.warn("onWebhookUpdateReceived.X: [CLIENT] Don't found bot command");
                return null;
            }

            final var requestMessage = tobaccoCommand.buildMessage(model);

            if (Objects.nonNull(requestMessage)) {
                execute(requestMessage);
            }

            return null;
        } catch (final TelegramApiException e) {
            log.error("Unhandled error: ", e);
            return null;
        }
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
