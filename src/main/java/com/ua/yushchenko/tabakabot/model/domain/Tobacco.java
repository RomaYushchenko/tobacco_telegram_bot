package com.ua.yushchenko.tabakabot.model.domain;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Tobacco {

    long tobaccoId;
    ItemType tobaccoName;
    TobaccoBotCommand tobaccoCommand;
    int cost25;
    int cost100;
    int cost250;
}
