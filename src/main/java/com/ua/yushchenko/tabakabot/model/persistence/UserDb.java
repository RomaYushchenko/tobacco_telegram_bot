package com.ua.yushchenko.tabakabot.model.persistence;

import com.ua.yushchenko.tabakabot.model.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity that reproduce User table
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Table(name = "tb_user")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDb {

    @Id
    @Column(name = "user_id", nullable = false)
    long userID;

    @Column(name = "user_name")
    String userName;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    UserRole userRole;

    @Column(name = "chat_id")
    Long chatId;
}
