package com.ua.yushchenko.tabakabot.service;

import com.ua.yushchenko.tabakabot.model.domain.User;
import com.ua.yushchenko.tabakabot.model.mapper.UserMapper;
import com.ua.yushchenko.tabakabot.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service that exposes the base functionality for interacting with {@link User} data
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    @NonNull
    public final UserRepository userRepository;
    @NonNull
    public final UserMapper userMapper;

    /**
     * Save user
     *
     * @param userToSave user to save
     * @return saved {@link User}
     */
    public User saveUser(final User userToSave) {
        log.info("saveUser.E: User to save: {}", userToSave);

        final var createdUser = userRepository.save(userMapper.domainToDb(userToSave));
        final User user = userMapper.dbToDomain(createdUser);

        log.info("saveUser.X: User {} is created", user.getUserID());
        return user;
    }

    /**
     * Gets user by user ID
     *
     * @param userId id of user
     * @return {@link User} by user ID
     */
    public User getUserById(final Long userId) {
        log.info("getUserById.E: user ID: {}", userId);

        final User user = userRepository.findById(userId)
                                        .map(userMapper::dbToDomain)
                                        .orElse(null);

        log.info("getUserById.X: user: {}", user);
        return user;

    }
}
