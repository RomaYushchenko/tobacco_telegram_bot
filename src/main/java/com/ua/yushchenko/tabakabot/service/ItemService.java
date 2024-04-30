package com.ua.yushchenko.tabakabot.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.mapper.ItemMapper;
import com.ua.yushchenko.tabakabot.repository.ItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service that exposes the base functionality for interacting with {@link Item} data
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ItemService {

    @NonNull
    private final ItemRepository itemRepository;
    @NonNull
    private final ItemMapper itemMapper;

    /**
     * Save item
     *
     * @param itemToSave item to save
     */
    public void saveItem(final Item itemToSave) {
        log.info("saveItem.E: Item to save: {}", itemToSave);

        final var createdItem = itemRepository.save(itemMapper.domainToDb(itemToSave));
        final var item = itemMapper.dbToDomain(createdItem);

        log.info("saveItem.X: Created tobacco item: {}", item);
    }

    /**
     * Gets list of {@link Item} by {@link ItemType}
     *
     * @param itemType type of item
     * @return list of {@link Item} by {@link ItemType}
     */
    public List<Item> getItemsByType(final ItemType itemType) {
        log.info("getItemsByType.E: item type: {}", itemType);

        final List<Item> items = itemRepository.findAllByItemType(itemType)
                                               .stream()
                                               .map(itemMapper::dbToDomain)
                                               .toList();

        log.info("getItemsByType.X: items: {}", items);
        return items;
    }

    /**
     * Gets {@link Item} by ID
     *
     * @param itemId ID of item
     * @return {@link Item} by ID
     */
    public Item getItemById(final long itemId) {
        log.info("getItemById.E: item ID: {}", itemId);

        final Item item = Optional.ofNullable(itemRepository.findItemDbByItemId(itemId))
                                  .map(itemMapper::dbToDomain)
                                  .orElse(null);

        log.info("getItemById.X: item: {}", item);
        return item;
    }

    public List<Item> getItemsByIds(final Collection<Long> itemIds) {
        return itemRepository.findAllByItemIdIsIn(itemIds)
                             .stream()
                             .map(itemMapper::dbToDomain)
                             .toList();
    }

    public List<Item> getAllItems() {
        final List<Item> items = new ArrayList<>();
        itemRepository.findAll()
                      .forEach(itemDb -> items.add(itemMapper.dbToDomain(itemDb)));
        return items;
    }

    /**
     * Remove item
     *
     * @param itemToSave item to remove
     */
    public void removeItem(final Item itemToSave) {
        log.info("removeItem.E: Item to remove: {}", itemToSave);

        itemRepository.delete(itemMapper.domainToDb(itemToSave));

        log.info("removeItem.X:");
    }
}
