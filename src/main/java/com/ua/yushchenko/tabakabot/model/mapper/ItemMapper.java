package com.ua.yushchenko.tabakabot.model.mapper;

import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.persistence.ItemDb;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item dbToDomain(final ItemDb itemDb) {
        return Item.builder()
                   .itemId(itemDb.getItemId())
                   .itemType(itemDb.getItemType())
                   .description(itemDb.getDescription())
                   .weight(itemDb.getWeight())
                   .build();
    }

    public ItemDb domainToDb(final Item item) {
        return ItemDb.builder()
                     .itemId(item.getItemId())
                     .itemType(item.getItemType())
                     .description(item.getDescription())
                     .weight(item.getWeight())
                     .build();
    }
}
