package com.ua.yushchenko.tabakabot.repository;

import java.util.Collection;
import java.util.List;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.persistence.ItemDb;
import com.ua.yushchenko.tabakabot.processor.command.client.BackCommand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to work with {@link ItemDb} table
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Repository
public interface ItemRepository extends CrudRepository<ItemDb, Long> {

    /**
     * Find all {@link  ItemDb} by {@link  ItemType}
     *
     * @param itemType type of item
     * @return list of {@link  ItemDb} by {@link  ItemType}
     */
    List<ItemDb> findAllByItemType(final ItemType itemType);

    /**
     * Find all {@link  ItemDb} by {@link  ItemType} and isAvailable
     * @param itemType type of item
     * @param available available
     * @return list of {@link  ItemDb} by {@link  ItemType} and isAvailable
     */
    List<ItemDb> findAllByItemTypeAndAvailable(final ItemType itemType, final Boolean available);

    /**
     * Find {@link  ItemDb} by ID of item
     *
     * @param itemId ID of item
     * @return {@link  ItemDb} by ID of item
     */
    ItemDb findItemDbByItemId(final long itemId);

    /**
     * Find all {@link  ItemDb} by list of IDs of item
     *
     * @param itemIds List of IDs of item
     * @return list of {@link  ItemDb} by list of IDs of item
     */
    List<ItemDb> findAllByItemIdIsIn(final Collection<Long> itemIds);
}
