package com.ua.yushchenko.tabakabot.common.setup;

import static com.ua.yushchenko.tabakabot.model.enums.ItemType.COAL;
import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_CLASSIC;
import static com.ua.yushchenko.tabakabot.model.enums.ItemType.TOBACCO_420_LIGHT;

import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.service.ItemService;
import com.ua.yushchenko.tabakabot.service.TobaccoService;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Class that provide logic to set up data to start application
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Component
@RequiredArgsConstructor
public class TobaccoItemSetUpData {

    @NonNull
    private final TobaccoService tobaccoService;
    @NonNull
    private final ItemService itemService;

    @PostConstruct
    public void setUpData() {
        setUpTobaccos();

        itemService.saveItem(Item.builder()
                                 .itemId(666)
                                 .itemType(COAL)
                                 .description("Unity (Кокосове вугілля)")
                                 .weight(1000)
                                 .available(true)
                                 .build());
    }

    private void setUpTobaccos() {
        final Tobacco tobacco420Light = Tobacco.builder()
                                               .tobaccoId(1)
                                               .tobaccoName(TOBACCO_420_LIGHT)
                                               .tobaccoCommand(TobaccoBotCommand.TABAKA_420_LIGHT)
                                               .cost25(120)
                                               .cost100(220)
                                               .cost250(400)
                                               .build();

        final Tobacco tobacco420Classic = Tobacco.builder()
                                                 .tobaccoId(2)
                                                 .tobaccoName(TOBACCO_420_CLASSIC)
                                                 .tobaccoCommand(TobaccoBotCommand.TABAKA_420_CLASSIC)
                                                 .cost25(120)
                                                 .cost100(220)
                                                 .cost250(400)
                                                 .build();

        tobaccoService.saveTobacco(tobacco420Light);
        tobaccoService.saveTobacco(tobacco420Classic);
    }
}
