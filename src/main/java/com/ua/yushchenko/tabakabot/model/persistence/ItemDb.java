package com.ua.yushchenko.tabakabot.model.persistence;

import com.ua.yushchenko.tabakabot.model.enums.ItemType;
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

@Table(name = "tb_item")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDb {

    @Id
    @Column(name = "item_id", nullable = false)
    long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    ItemType itemType;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "weight", nullable = false)
    int weight;
}
