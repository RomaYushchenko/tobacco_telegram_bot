package com.ua.yushchenko.tabakabot.model.mapper;

import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import com.ua.yushchenko.tabakabot.model.persistence.TobaccoDb;
import org.springframework.stereotype.Component;

@Component
public class TobaccoMapper {

    public Tobacco dbToDomain(final TobaccoDb tobaccoDb) {
        return Tobacco.builder()
                      .tobaccoId(tobaccoDb.getTobaccoId())
                      .tobaccoName(tobaccoDb.getTobaccoName())
                      .tobaccoCommand(tobaccoDb.getTobaccoCommand())
                      .cost25(tobaccoDb.getCost25())
                      .cost100(tobaccoDb.getCost100())
                      .cost250(tobaccoDb.getCost250())
                      .build();
    }

    public TobaccoDb domainToDb(final Tobacco tobacco) {
        return TobaccoDb.builder()
                        .tobaccoId(tobacco.getTobaccoId())
                        .tobaccoName(tobacco.getTobaccoName())
                        .tobaccoCommand(tobacco.getTobaccoCommand())
                        .cost25(tobacco.getCost25())
                        .cost100(tobacco.getCost100())
                        .cost250(tobacco.getCost250())
                        .build();
    }
}
