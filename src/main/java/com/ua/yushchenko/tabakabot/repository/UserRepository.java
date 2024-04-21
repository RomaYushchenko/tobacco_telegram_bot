package com.ua.yushchenko.tabakabot.repository;

import com.ua.yushchenko.tabakabot.model.persistence.UserDb;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to work with {@link UserDb} table
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Repository
public interface UserRepository extends CrudRepository<UserDb, Long> {

}
