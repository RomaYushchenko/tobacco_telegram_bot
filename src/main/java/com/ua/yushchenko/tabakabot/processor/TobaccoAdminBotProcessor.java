package com.ua.yushchenko.tabakabot.processor;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.LOAD_420_CLASSIC;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.LOAD_420_LIGHT;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.getEnumByString;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.getFirstTextOfMessageEntityBotCommand;

import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.InformationMessageBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.admin.LoadTobaccoBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.admin.MenuBuilder;
import com.ua.yushchenko.tabakabot.builder.ui.admin.OrderListBuilder;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@RequiredArgsConstructor
public class TobaccoAdminBotProcessor extends TelegramWebhookBot {

    @NonNull
    private final InformationMessageBuilder informationMessageBuilder;
    @NonNull
    private final MenuBuilder menuBuilder;
    @NonNull
    private final OrderListBuilder orderListBuilder;
    @NonNull
    private final LoadTobaccoBuilder loadTobaccoBuilder;

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

                        execute(menuBuilder.buildTobaccoAdminMenu(chatId));

                        log.info("onUpdateReceived.X: Finish processing {} command", botCommand);

                        return null;
                    } else if (Objects.equals(botCommand, LOAD_420_LIGHT.getCommandString())) {
                        execute(loadTobaccoBuilder.buildLoad420LightTobacco(chatId, message));

                        return null;
                    } else if (Objects.equals(botCommand, LOAD_420_CLASSIC.getCommandString())) {
                        execute(loadTobaccoBuilder.buildLoad420ClassicTobacco(chatId, message));

                        return null;
                    } else {
                        execute(informationMessageBuilder.buildSendMessage(chatId, "Command is not support"));
                        log.info("onUpdateReceived.X: Command {} is not support", botCommand);

                        return null;
                    }
                } else {
                    execute(informationMessageBuilder.buildSendMessage(chatId, "It is not command"));

                    return null;
                }
            } else if (update.hasCallbackQuery()) {
                log.info("onUpdateReceived.E: Starting processing callback query");
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final Message message = callbackQuery.getMessage();
                final Long chatId = message.getChatId();
                final Integer messageId = message.getMessageId();
                final String data = callbackQuery.getData();

                final TobaccoBotCommand tobaccoBotCommand = TobaccoBotCommand.getEnumByString(data);

                if (Objects.isNull(tobaccoBotCommand)) {
                    execute(informationMessageBuilder.buildSendMessage(chatId, "Unhandled callback command!!!"));
                    return null;
                }

                switch (tobaccoBotCommand) {
                    case GET_ALL_ORDERS_BY_USER -> {
                        execute(orderListBuilder.buildTobaccoAdminOrderListByUserMenu(chatId, messageId));
                        return null;
                    }
                    case GET_ALL_ORDERS -> {
                        execute(orderListBuilder.buildTobaccoAdminOrderListByAllUserMenu(chatId, messageId));
                        return null;
                    }
                    case BACK -> {
                        final String[] splitBotCommand = data.split(":");
                        final TobaccoBotCommand secondCommand = getEnumByString(splitBotCommand[1]);

                        if (Objects.isNull(secondCommand)) {
                            log.error("Second bot command is null");
                            return null;
                        }

                        if (secondCommand == START) {
                            execute(menuBuilder.buildTobaccoAdminMenu(chatId, messageId));
                        } else {
                            log.error("Unhandled second command: {}", secondCommand);
                        }

                        return null;
                    }
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
}
