package com.ua.yushchenko.tabakabot.model.domain;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import lombok.Builder;
import lombok.Value;

/**
 * Represents domain order list context entity.
 *
 * @author romanyushchenko
 * @version v.0.1
 */
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
