package com.ua.yushchenko.tabakabot.processor.command.client;

import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_CLASSIC;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoOrderMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Class that represents the processing of the {@link TobaccoCommand} {@link TobaccoBotCommand#TABAKA_420_CLASSIC}
 * for Client
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Tobacco420ClassicCommand implements TobaccoCommand {

    @NonNull
    private final TobaccoOrderMenuBuilder tobaccoOrderMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();

        final var sendMessage =
                tobaccoOrderMenuBuilder.buildTobaccoOrderMenu(chatId, messageId, TOBACCO_420_CLASSIC, getCommand());
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.TABAKA_420_CLASSIC;
    }
}
