package com.ua.yushchenko.tabakabot.service;

import java.util.List;

import com.ua.yushchenko.tabakabot.common.setup.CSVHelper;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShutdownHookService {

    @NonNull
    private final ItemService itemService;
    @NonNull
    private final OrderService orderService;

    @PreDestroy
    public void onShutdown() {
        final List<Item> allItems = itemService.getAllItems();
        CSVHelper.itemsToCsv(allItems, "items.csv");

        final List<Order> allOrders = orderService.getAllOrders();
        CSVHelper.orderToCsv(allOrders, "orders.csv");
    }
}
