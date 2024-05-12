package com.ua.yushchenko.tabakabot.processor.common;

import java.util.Objects;

import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.domain.UserRequestModel;
import com.ua.yushchenko.tabakabot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Class that represents the processing User
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class UserProcessor {

    @NonNull
    private final UserService userService;

    /**
     * Pre-processing current user for updating data
     *
     * @param model model of user request
     */
    public void preProcessingUser(final UserRequestModel model) {
        final User user = model.getUser();
        final Long chatId = model.getChatId();
        final long userID = user.getUserID();

        log.info("preProcessingUser.E [chatId:{}], [userId:{}]", chatId, userID);

        final User existingUser = userService.getUserById(userID);

        if (Objects.isNull(existingUser)) {
            log.warn("preProcessingUser.X User [userId:{}] doesn't exist in system", userID);
            return;
        }

        if (Objects.nonNull(existingUser.getChatId())) {
            log.info("preProcessingUser.X User [userId:{}] has chatId:{}", userID, existingUser.getChatId());
            return;
        }

        final User userToUpdate = existingUser.toBuilder()
                                              .chatId(chatId)
                                              .build();

        userService.saveUser(userToUpdate);

        log.info("preProcessingUser.X User [userId:{}] upgraded; Added [chatId:{}]", chatId, userID);
    }
}
