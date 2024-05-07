package com.ua.yushchenko.tabakabot.processor.command.client;

import java.util.Objects;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Class that represents the processing of the {@link TobaccoCommand} {@link TobaccoBotCommand#START} for Client
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component("startCommandOfClient")
@RequiredArgsConstructor
public class StartCommand implements TobaccoCommand {

    @NonNull
    private final UserService userService;
    @NonNull
    private final TobaccoMenuBuilder tobaccoMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: Processing {} command", getCommand());
        final User user = model.getUser();

        if (Objects.isNull(userService.getUserById(user.getUserID()))) {
            userService.saveUser(user);
        }

        final var sendMessage = tobaccoMenuBuilder.buildTobaccoMenu(model.getChatId());
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.START;
    }
}
