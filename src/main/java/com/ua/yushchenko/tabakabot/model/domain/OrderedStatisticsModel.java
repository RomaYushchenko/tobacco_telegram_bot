package com.ua.yushchenko.tabakabot.model.domain;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import lombok.Builder;
import lombok.Value;

/**
 * Represents domain ordered statistics model entity.
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Value
@Builder(toBuilder = true)
public class OrderedStatisticsModel {

    long tobaccoItemId;
    ItemType itemType;
    String description;
    Long cost;
    Long count;

}
