package com.ua.yushchenko.tabakabot.repository;

import java.util.List;

import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import com.ua.yushchenko.tabakabot.model.persistence.OrderDb;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository to work with {@link OrderDb} table
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Repository
public interface OrderRepository extends CrudRepository<OrderDb, Long> {

    /**
     * Find all {@link OrderDb} by ID of user
     *
     * @param userId ID of user
     * @return list of {@link OrderDb} by ID of user
     */
    List<OrderDb> findAllByUserId(final long userId);

    /**
     * Find all {@link OrderDb} by ID of user and Order status
     *
     * @param userId ID of user
     * @return list of {@link OrderDb} by ID of user
     */
    //@Query("SELECT r FROM OrderDb r WHERE r.orderStatus = :#{#orderStatus} AND r.userId = :userId")
    List<OrderDb> findAllByUserIdAndOrderStatus(final /*@Param("userId")*/ long userId,
                                                final /*@Param("orderStatus")*/ OrderStatus orderStatus);

    /**
     * Find all {@link OrderDb} by ID of user and ID of tobacco item
     *
     * @param userId        ID of user
     * @param tobaccoItemId ID of tobacco item
     * @return list of {@link OrderDb} by ID of user and ID of tobacco item
     */
    List<OrderDb> findAllByUserIdAndAndTobaccoItemId(final long userId, final long tobaccoItemId);
}
