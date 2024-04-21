package com.ua.yushchenko.tabakabot.builder.ui.admin;


import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_CLASSIC;
import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_LIGHT;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.BACK;
import static com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand.START;
import static com.ua.yushchenko.tabakabot.utility.TobaccoBotCommandUtility.mergeBotCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ua.yushchenko.tabakabot.builder.ui.InlineButtonBuilder;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoadTobaccoBuilder {

    @NonNull
    private final InlineButtonBuilder buttonBuilder;
    @NonNull
    private final ItemService itemService;

    public SendMessage buildLoad420LightTobacco(final Long chatId, final Message message) {
        final String[] tobaccos = message.getText().split("\n");
        final List<String> tobaccosList = new ArrayList<>(List.of(tobaccos));
        tobaccosList.remove(0);

        final AtomicInteger counter = new AtomicInteger(0);

        List<Item> items = tobaccosList.stream()
                                       .map(tobaccoItem -> {
                                           final String weight = tobaccoItem.substring(tobaccoItem.indexOf("(") + 1,
                                                                                       tobaccoItem.indexOf(")") - 2);
                                           final String description = tobaccoItem.substring(
                                                   tobaccoItem.indexOf("Light") + "Light".length() + 1,
                                                   tobaccoItem.indexOf("(") - 1);

                                           return Item.builder()
                                                      .itemId(counter.incrementAndGet())
                                                      .itemType(TOBACCO_420_LIGHT)
                                                      .description(description)
                                                      .weight(Integer.parseInt(weight))
                                                      .build();
                                       })
                                       .toList();

        items.forEach(itemService::saveItem);

        return SendMessage.builder()
                          .chatId(chatId)
                          .text(TOBACCO_420_LIGHT.getItemString() + " was loaded")
                          .build();
    }

    public SendMessage buildLoad420ClassicTobacco(final Long chatId, final Message message) {
        final String[] tobaccos = message.getText().split("\n");
        final List<String> tobaccosList = new ArrayList<>(List.of(tobaccos));
        tobaccosList.remove(0);

        final AtomicInteger counter = new AtomicInteger(50);

        List<Item> items = tobaccosList.stream()
                                       .map(tobaccoItem -> {
                                           final String weight = tobaccoItem.substring(tobaccoItem.indexOf(", ") + 2,
                                                                                       tobaccoItem.indexOf(")") - 2);
                                           final String description = tobaccoItem.substring(
                                                   tobaccoItem.indexOf("Табак 420") + "Табак 420".length() + 1,
                                                   tobaccoItem.indexOf(", ") - 1) + ")";

                                           return Item.builder()
                                                      .itemId(counter.incrementAndGet())
                                                      .itemType(TOBACCO_420_CLASSIC)
                                                      .description(description)
                                                      .weight(Integer.parseInt(weight))
                                                      .build();
                                       })
                                       .toList();

        items.forEach(itemService::saveItem);

        return SendMessage.builder()
                   .chatId(chatId)
                   .text(TOBACCO_420_CLASSIC.getItemString() + " was loaded")
                   .build();
    }
}
