package com.ua.yushchenko.tabakabot.model.mapper;

import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.persistence.UserDb;
import org.springframework.stereotype.Component;

/**
 * Represents mapping persistence to domain entity
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Component
public class UserMapper {

    public User apiToDomain(final org.telegram.telegrambots.meta.api.objects.User userApi) {
        return User.builder()
                   .userID(userApi.getId())
                   .userName(userApi.getUserName())
                   .firstName(userApi.getFirstName())
                   .lastName(userApi.getLastName())
                   .build();
    }

    public User dbToDomain(final UserDb userDb) {
        return User.builder()
                   .userID(userDb.getUserID())
                   .userName(userDb.getUserName())
                   .firstName(userDb.getFirstName())
                   .lastName(userDb.getLastName())
                   .build();
    }

    public UserDb domainToDb(final User user) {
        return UserDb.builder()
                     .userID(user.getUserID())
                     .userName(user.getUserName())
                     .firstName(user.getFirstName())
                     .lastName(user.getLastName())
                     .build();
    }
}
