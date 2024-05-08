package com.ua.yushchenko.tabakabot.processor.command.admin;

import java.util.List;

import com.ua.yushchenko.tabakabot.builder.ui.admin.UserMenuBuilder;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Class that represents the processing of the {@link TobaccoCommand} {@link TobaccoBotCommand#GET_ALL_ORDERS} for Admin
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllUsersCommand implements TobaccoCommand {

    @NonNull
    private final UserService userService;
    @NonNull
    private final UserMenuBuilder userMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final UserRequestModel model) {
        log.info("execute.E: [ADMIN] Processing {} command", getCommand());

        final Long chatId = model.getChatId();
        final Integer messageId = model.getMessageId();

        final List<User> users = userService.getAllUsers();

        final EditMessageText messageText = userMenuBuilder.build(chatId, messageId, users);

        log.info("execute.X: [ADMIN] Processed {} command", getCommand());
        return messageText;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.GET_ALL_USERS;
    }
}
