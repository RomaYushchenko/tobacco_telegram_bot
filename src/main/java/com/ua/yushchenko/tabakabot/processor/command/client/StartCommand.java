package com.ua.yushchenko.tabakabot.processor.command.client;

import com.ua.yushchenko.tabakabot.builder.ui.client.TobaccoMenuBuilder;
import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.model.mapper.UserMapper;
import com.ua.yushchenko.tabakabot.processor.command.TobaccoCommand;
import com.ua.yushchenko.tabakabot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements TobaccoCommand {

    @NonNull
    private final UserService userService;
    @NonNull
    private final UserMapper userMapper;
    @NonNull
    private final TobaccoMenuBuilder tobaccoMenuBuilder;

    @Override
    public BotApiMethod<?> buildMessage(final Update update, final User user) {
        log.info("execute.E: Processing {} command", getCommand());
        final Message message = update.getMessage();
        final Long chatId = update.getMessage().getChatId();

        //final User user = userMapper.apiToDomain(message.getFrom());
        userService.saveUser(user);

        final var sendMessage = tobaccoMenuBuilder.buildTobaccoMenu(chatId);
        log.info("execute.X: Processed {} command", getCommand());
        return sendMessage;
    }

    @Override
    public TobaccoBotCommand getCommand() {
        return TobaccoBotCommand.START;
    }
}
