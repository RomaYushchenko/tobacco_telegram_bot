package com.ua.yushchenko.tabakabot.processor.command.admin;

import com.ua.yushchenko.tabakabot.builder.ui.admin.MenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Slf4j
@Component("startCommandOfAdmin")
@RequiredArgsConstructor
public class StartCommand implements TobaccoCommand {

    @NonNull
    private final MenuBuilder menuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: [ADMIN] Processing {} command", getCommand());

        final var sendMessage = menuBuilder.buildTobaccoAdminMenu(model.getChatId());

        log.info("execute.X: [ADMIN] Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.START;
    }
}
