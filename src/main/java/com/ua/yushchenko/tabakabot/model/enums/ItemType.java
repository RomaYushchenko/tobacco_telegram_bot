package com.ua.yushchenko.tabakabot.model.enums;

import java.util.Objects;

/**
 * Enum to provide type of item
 *
 * @author romanyushchenko
 * @version v.0.1
 */
public enum ItemType {

    TOBACCO_420_LIGHT("420 Light"),
    TOBACCO_420_CLASSIC("420 Classic"),
    COAL("Coal");

    private final String itemString;

    ItemType(final String itemString) {
        this.itemString = itemString;
    }

    public String getItemString() {
        return this.itemString;
    }

    public static ItemType getEnumByString(String itemString) {
        for (final ItemType itemType : ItemType.values()) {
            if (Objects.equals(itemType.getItemString(), itemString)) {
                return itemType;
            }
        }

        return null;
    }
}
