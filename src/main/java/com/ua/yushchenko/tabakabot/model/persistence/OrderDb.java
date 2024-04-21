package com.ua.yushchenko.tabakabot.model.persistence;

import java.time.LocalDateTime;

import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity that reproduce Order table
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Table(name = "tb_order")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    long orderId;

    @Column(name = "user_id", nullable = false)
    long userId;

    @Column(name = "tobacco_item_id", nullable = false)
    long tobaccoItemId;

    @Column(name = "order_time", nullable = false)
    LocalDateTime orderTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    OrderStatus orderStatus;

}
