package com.ua.yushchenko.tabakabot.processor.command.admin;

import com.ua.yushchenko.tabakabot.builder.ui.admin.LoadTobaccoBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Class that represents the processing of the {@link TobaccoCommand} {@link TobaccoBotCommand#LOAD_420_CLASSIC} for
 * Admin
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Load420ClassicCommand implements TobaccoCommand {

    @NonNull
    private final LoadTobaccoBuilder loadTobaccoBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: [ADMIN] Processing {} command", getCommand());

        final var sendMessage =
                loadTobaccoBuilder.buildLoad420ClassicTobacco(model.getChatId(), model.getMessageText());

        log.info("execute.X: [ADMIN] Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.LOAD_420_CLASSIC;
    }
}
