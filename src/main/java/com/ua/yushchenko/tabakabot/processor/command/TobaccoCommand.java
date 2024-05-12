package com.ua.yushchenko.tabakabot.processor.command;

import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Interface that represents base processing of the {@link TobaccoCommand} {@link TobaccoBotCommand}
 *
 * @author romanyushchenko
 * @version v.0.1
 */
public interface TobaccoCommand {

    /**
     * Build {@link BotApiMethod} based on {@link TobaccoBotCommand} by {@link UserRequestModel}
     *
     * @param requestModel model of user request
     * @return {@link BotApiMethod} based on {@link TobaccoBotCommand} by {@link UserRequestModel}
     */
    BotApiMethod<?> buildMessage(final UserRequestModel requestModel);

    /**
     * Get {@link TobaccoBotCommand} for current instance
     *
     * @return {@link TobaccoBotCommand}
     */
    TobaccoBotCommand getCommand();
}
