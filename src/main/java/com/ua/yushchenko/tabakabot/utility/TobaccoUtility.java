package com.ua.yushchenko.tabakabot.utility;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;

/**
 * Utility class that provided functional for work with Tobacco.
 *
 * @author romanyushchenko
 * @version v.0.1
 */
public class TobaccoUtility {

    /**
     * @param tobaccoType   type of Tobacco
     * @param weight        weight of Tobacco
     * @param tobaccoToType map of type of tobacco
     * @return
     */
    public static int getCostTobacco(final ItemType tobaccoType,
                                     final int weight,
                                     final Map<ItemType, List<Tobacco>> tobaccoToType) {
        if (Objects.equals(tobaccoType, ItemType.COAL)) {
            return 240;
        } else {
            final Tobacco tobacco = tobaccoToType.get(tobaccoType).get(0);
            return getCostByWeight(tobacco, weight);
        }
    }

    private static int getCostByWeight(final Tobacco tobacco, final int weight) {
        return switch (weight) {
            case 50 -> tobacco.getCost25();
            case 100 -> tobacco.getCost100();
            case 250 -> tobacco.getCost250();
            default -> 0;
        };
    }
}
