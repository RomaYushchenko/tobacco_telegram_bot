package com.ua.yushchenko.tabakabot.model.domain;

import java.util.Objects;

import com.ua.yushchenko.tabakabot.model.enums.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * Represents domain user entity.
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Data
@Builder(toBuilder = true)
public class User {

    long userID;
    String userName;
    String firstName;
    String lastName;
    @Setter(value = AccessLevel.PRIVATE)
    String fullName;

    UserRole userRole;

    public String getLinkName() {
        return Objects.nonNull(getUserName())
                ? getUserName()
                : getFirstName() + " " + getLastName();
    }
}
