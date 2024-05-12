package com.ua.yushchenko.tabakabot.builder;

import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.getListCommandsByString;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.getFirstCommandOfMessageEntityBotCommand;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.isTobaccoBotCommand;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.model.mapper.UserMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Represents of builder for {@link UserRequestModel} based on {@link Update}
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class UserRequestModelBuilder {

    @NonNull
    private final UserMapper userMapper;

    /**
     * Builds {@link UserRequestModel} by {@link Update}
     *
     * @param update instance of {@link Update}
     * @return {@link UserRequestModel} by {@link Update}
     */
    public UserRequestModel build(final Update update) {
        final Long chatId = getChatId(update);
        final User user = getUser(update, chatId);

        log.info("build.E: Building request model for [userID:{}] and [chatID:{}]", user.getUserID(), chatId);

        final Message message = getMessage(update);

        final var userRequestModel =
                UserRequestModel.builder()
                                .chatId(chatId)
                                .user(user)
                                .messageId(message.getMessageId())
                                .messageText(message.getText())
                                .isCallbackQuery(update.hasCallbackQuery())
                                .tobaccoBotCommands(getTobaccoBotCommands(update))
                                .build();

        log.info("build.X: Built request model for [userID:{}] and [chatID:{}]; Model:{}",
                 user.getUserID(), chatId, userRequestModel);

        return userRequestModel;
    }

    private Long getChatId(final Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }

        throw new IllegalArgumentException("Cannot find chat ID");
    }

    private User getUser(final Update update, final Long chatId) {
        if (update.hasMessage()) {
            return userMapper.apiToDomain(update.getMessage().getFrom(), chatId);
        } else if (update.hasCallbackQuery()) {
            return userMapper.apiToDomain(update.getCallbackQuery().getFrom(), chatId);
        }

        throw new IllegalArgumentException("Cannot find User");
    }

    private Message getMessage(final Update update) {
        if (update.hasMessage()) {
            return update.getMessage();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage();
        }

        throw new IllegalArgumentException("Cannot find Message");
    }

    private List<Object> getTobaccoBotCommands(final Update update) {
        if (update.hasMessage()) {
            final Message message = update.getMessage();

            if (isTobaccoBotCommand(message)) {
                final var botCommand = getFirstCommandOfMessageEntityBotCommand(message);
                return Objects.nonNull(botCommand)
                        ? Collections.singletonList(botCommand)
                        : Collections.emptyList();
            }

            return Collections.emptyList();
        } else if (update.hasCallbackQuery()) {
            return getListCommandsByString(update.getCallbackQuery().getData());
        }

        throw new IllegalArgumentException("Cannot find Bot Command");
    }
}
