package com.ua.yushchenko.tabakabot.processor.command;

import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface TobaccoCommand {

    BotApiMethod<?> buildMessage(final UserRequestModel requestModel);

    TobaccoBotCommand getCommand();
}
