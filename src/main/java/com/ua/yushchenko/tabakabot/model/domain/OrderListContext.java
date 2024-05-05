package com.ua.yushchenko.tabakabot.model.domain;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class OrderListContext {

    long tobaccoItemId;
    User user;
    ItemType itemType;
    String description;
    int wight;
    int cost;
    int count;
}
