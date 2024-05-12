package com.ua.yushchenko.tabakabot.repository;

import com.ua.yushchenko.tabakabot.model.persistence.TobaccoDb;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to work with {@link TobaccoDb} table
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Repository
public interface TobaccoRepository extends CrudRepository<TobaccoDb, Long> {

}
