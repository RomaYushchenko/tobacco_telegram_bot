package com.ua.yushchenko.tabakabot.processor;

import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.UserRequestModelBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.processor.command.admin.AdminCommandFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@Component
@RequiredArgsConstructor
public class TobaccoAdminBotProcessor extends TelegramWebhookBot {

    @Value("${telegram.tobacco.admin.bot.token}")
    private String tobaccoAdminBotToken;

    @NonNull
    private final AdminCommandFactory adminCommandFactory;
    @NonNull
    private final UserRequestModelBuilder userRequestModelBuilder;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(final Update update) {
        try {
            final UserRequestModel model = userRequestModelBuilder.build(update);

            final TobaccoCommand tobaccoCommand = adminCommandFactory.retrieveCommand(model.getTobaccoBotCommands());

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
