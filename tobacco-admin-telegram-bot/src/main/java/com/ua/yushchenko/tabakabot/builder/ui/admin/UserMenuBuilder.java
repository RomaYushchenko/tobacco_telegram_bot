package com.ua.yushchenko.tabakabot.builder.ui.admin;

import java.util.List;

import com.ua.yushchenko.tabakabot.builder.ui.CustomButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Represents of builder for {@link EditMessageText} based on User Menu
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class UserMenuBuilder {

    @NonNull
    private final CustomButtonBuilder buttonBuilder;

    /**
     * Builds Users Menu
     *
     * @param chatId    ID of chat
     * @param messageId ID of message
     * @param users     list of users
     * @return {@link EditMessageText} with User menu
     */
    public EditMessageText build(final Long chatId, final Integer messageId, final List<User> users) {
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(buildUsersInfo(users))
                              .replyMarkup(InlineKeyboardMarkup.builder()
                                                               .keyboardRow(buttonBuilder.buildBackToStartButtons())
                                                               .build())
                              .build();
    }

    private String buildUsersInfo(final List<User> users) {
        final StringBuilder usersInfo = new StringBuilder();

        usersInfo.append("List of the users:")
                 .append("\n");

        users.forEach(user -> usersInfo.append(user.getUserID())
                                       .append(") ")
                                       .append(user.getLinkName())
                                       .append("\n"));

        return usersInfo.toString();
    }
}
