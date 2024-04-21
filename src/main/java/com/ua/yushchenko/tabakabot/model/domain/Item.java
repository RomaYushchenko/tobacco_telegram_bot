package com.ua.yushchenko.tabakabot.model.domain;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Item {

    long itemId;
    ItemType itemType;
    String description;
    int weight;
}
