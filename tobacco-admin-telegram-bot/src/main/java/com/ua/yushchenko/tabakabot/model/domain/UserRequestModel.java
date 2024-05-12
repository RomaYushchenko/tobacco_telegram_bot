package com.ua.yushchenko.tabakabot.model.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Represents domain user request model entity.
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Data
@Builder(toBuilder = true)
public class UserRequestModel {

    final Long chatId;
    final Integer messageId;
    @ToString.Exclude
    final String messageText;
    final User user;
    final boolean isCallbackQuery;
    final List<Object> tobaccoBotCommands;
}
