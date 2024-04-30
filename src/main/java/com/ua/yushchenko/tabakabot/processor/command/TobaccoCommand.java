package com.ua.yushchenko.tabakabot.processor.command;

import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TobaccoCommand {

    BotApiMethod<?> buildMessage(final Update update, final User user);

    TobaccoBotCommand getCommand();
}
