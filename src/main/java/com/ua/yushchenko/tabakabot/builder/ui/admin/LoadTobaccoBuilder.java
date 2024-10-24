package com.ua.yushchenko.tabakabot.builder.ui.admin;


import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_CLASSIC;
import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_LIGHT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.service.ItemService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Represents of builder for {@link SendMessage} based on Load Tobacco
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class LoadTobaccoBuilder {

    @NonNull
    private final ItemService itemService;

    /**
     * Build {@link SendMessage} for Load 420 Light
     *
     * @param chatId      ID of chat
     * @param messageText text of message
     * @return {@link SendMessage} for Load 420 Light
     */
    public SendMessage buildLoad420LightTobacco(final Long chatId, final String messageText) {
        final List<String> tobaccosList = getTobaccosFromBotMessage(messageText);

        final Map<String, Item> currentItemsToDescription =
                itemService.getItemsByType(TOBACCO_420_LIGHT)
                           .stream()
                           .collect(Collectors.toMap(Item::getDescription, Function.identity()));

        final Map<String, Item> itemsToLoadToDescription =
                tobaccosList.stream()
                            .map(LoadTobaccoBuilder::buildItemOf420Light)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toMap(Item::getDescription, Function.identity()));

        processLoadItemsToDisable(currentItemsToDescription, itemsToLoadToDescription);
        processLoadItemsToAdd(currentItemsToDescription, itemsToLoadToDescription, TOBACCO_420_LIGHT);
        processLoadItemsToAvailable(currentItemsToDescription, itemsToLoadToDescription);

        return SendMessage.builder()
                          .chatId(chatId)
                          .text(TOBACCO_420_LIGHT.getItemString() + " was loaded")
                          .build();
    }

    /**
     * Build {@link SendMessage} for Load 420 Classic
     *
     * @param chatId      ID of chat
     * @param messageText text of message
     * @return {@link SendMessage} for Load 420 Classic
     */
    public SendMessage buildLoad420ClassicTobacco(final Long chatId, final String messageText) {
        final List<String> tobaccosList = getTobaccosFromBotMessage(messageText);

        final Map<String, Item> currentItemsToDescription =
                itemService.getItemsByType(TOBACCO_420_CLASSIC)
                           .stream()
                           .collect(Collectors.toMap(Item::getDescription, Function.identity()));

        final Map<String, Item> itemsToLoadToDescription =
                tobaccosList.stream()
                            .map(LoadTobaccoBuilder::buildItemOf420Classic)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toMap(Item::getDescription, Function.identity()));

        processLoadItemsToDisable(currentItemsToDescription, itemsToLoadToDescription);
        processLoadItemsToAdd(currentItemsToDescription, itemsToLoadToDescription, TOBACCO_420_CLASSIC);
        processLoadItemsToAvailable(currentItemsToDescription, itemsToLoadToDescription);

        return SendMessage.builder()
                          .chatId(chatId)
                          .text(TOBACCO_420_CLASSIC.getItemString() + " was loaded")
                          .build();
    }

    private static Item buildItemOf420Classic(final String tobaccoItem) {
        if  (StringUtils.isBlank(tobaccoItem) || tobaccoItem.length() < 3) {
            log.warn("buildItemOf420Classic.X: tobaccoItem string is not valid. {}", tobaccoItem);
            return null;
        }

        final String weight = tobaccoItem.substring(tobaccoItem.indexOf(", ") + 2,
                                                    tobaccoItem.indexOf(")") - 2);
        final String description = tobaccoItem.substring(
                tobaccoItem.indexOf("Табак 420") + "Табак 420".length() + 1,
                tobaccoItem.indexOf(", ") - 1) + ")";

        return Item.builder()
                   .itemType(TOBACCO_420_CLASSIC)
                   .description(description)
                   .weight(Integer.parseInt(weight))
                   .build();
    }

    private static Item buildItemOf420Light(final String tobaccoItem) {
        if  (StringUtils.isBlank(tobaccoItem) || tobaccoItem.length() < 3) {
            log.warn("buildItemOf420Light.X: tobaccoItem string is not valid. {}", tobaccoItem);
            return null;
        }

        final String weight =
                tobaccoItem.substring(tobaccoItem.indexOf("(") + 1,
                                      tobaccoItem.indexOf(")") - 2);
        final String description = tobaccoItem.substring(
                tobaccoItem.indexOf("Light") + "Light".length() + 1,
                tobaccoItem.indexOf("(") - 1);

        return Item.builder()
                   .itemType(TOBACCO_420_LIGHT)
                   .description(description)
                   .weight(Integer.parseInt(weight))
                   .build();
    }

    private void processLoadItemsToAdd(final Map<String, Item> currentItemsToDescription,
                                       final Map<String, Item> itemsToLoadToDescription,
                                       final ItemType itemType) {
        long count = getLastItemId(currentItemsToDescription, itemType);

        final AtomicLong counter = new AtomicLong(count);

        Sets.difference(itemsToLoadToDescription.keySet(), currentItemsToDescription.keySet())
            .stream()
            .map(descriptionToAdd -> {
                final var newItem = itemsToLoadToDescription.get(descriptionToAdd);

                final var itemToAdd = newItem.toBuilder()
                                             .itemId(counter.incrementAndGet())
                                             .available(true)
                                             .build();

                log.info("processLoadItemsToAdd.E: available new item: {}", itemToAdd);

                return itemToAdd;
            })
            .toList()
            .forEach(itemService::saveItem);
    }

    private void processLoadItemsToDisable(final Map<String, Item> currentItemsToDescription,
                                           final Map<String, Item> itemsToLoadToDescription) {
        Sets.difference(currentItemsToDescription.keySet(), itemsToLoadToDescription.keySet())
            .stream()
            .map(descriptionToRemove -> {
                final var currentItem = currentItemsToDescription.get(descriptionToRemove);

                log.info("processLoadItemsToDisable.E: disabled item: {}", currentItem);

                return currentItem.toBuilder()
                                  .available(false)
                                  .build();
            })
            .toList()
            .forEach(itemService::saveItem);
    }

    private void processLoadItemsToAvailable(final Map<String, Item> currentItemsToDescription,
                                             final Map<String, Item> itemsToLoadToDescription) {
        final List<Item> itemsToAvailable = new ArrayList<>();

        itemsToLoadToDescription.forEach((description, loadItems) -> {
            if (currentItemsToDescription.containsKey(description)
                    && !currentItemsToDescription.get(description).isAvailable()) {

                final var currentItem = currentItemsToDescription.get(description);

                log.info("processLoadItemsToAvailable.E: disabled item: {}", currentItem);

                itemsToAvailable.add(currentItem.toBuilder()
                                                .available(true)
                                                .build());
            }
        });

        itemsToAvailable.forEach(itemService::saveItem);
    }

    private long getLastItemId(final Map<String, Item> currentItemsToDescription, final ItemType itemType) {
        final Collection<Item> values = currentItemsToDescription.values();

        int count = 0;

        if (Objects.equals(itemType, TOBACCO_420_CLASSIC)) {
            count = 50;
        }

        final List<Item> itemsSortedById = values.stream()
                                                 .sorted(Comparator.comparing(Item::getItemId))
                                                 .toList();

        if (values.size() > 0) {
            count = itemsSortedById.size() - 1;
        } else {
            return count;
        }

        return itemsSortedById.get(count).getItemId();
    }

    private List<String> getTobaccosFromBotMessage(final String messageText) {
        final String[] tobaccos = messageText.split("\n");
        final List<String> tobaccosList = new ArrayList<>(List.of(tobaccos));
        tobaccosList.remove(0);

        return tobaccosList;
    }
}
