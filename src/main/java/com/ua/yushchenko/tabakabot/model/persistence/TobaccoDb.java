package com.ua.yushchenko.tabakabot.model.persistence;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import com.ua.yushchenko.tabakabot.model.enums.TobaccoBotCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity that reproduce Tobacco table
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Table(name = "tb_tobacco")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TobaccoDb {

    @Id
    @Column(name = "tobacco_id", nullable = false)
    long tobaccoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tobacco_name", nullable = false)
    ItemType tobaccoName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tobacco_command", nullable = false)
    TobaccoBotCommand tobaccoCommand;

    @Column(name = "cost_25", nullable = false)
    int cost25;

    @Column(name = "cost_100", nullable = false)
    int cost100;

    @Column(name = "cost_250", nullable = false)
    int cost250;

}
