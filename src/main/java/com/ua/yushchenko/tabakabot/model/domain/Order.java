package com.ua.yushchenko.tabakabot.model.domain;

import java.time.LocalDateTime;

import com.ua.yushchenko.tabakabot.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Value;

/**
 * Represents domain order entity.
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Value
@Builder(toBuilder = true)
public class Order {

    long orderId;
    long userId;
    long tobaccoItemId;
    LocalDateTime orderTime;
    OrderStatus orderStatus;
}
