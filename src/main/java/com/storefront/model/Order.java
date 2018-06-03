package com.storefront.model;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
public class Order {

    @NonNull
    private String guid;

    @NonNull
    private List<OrderStatusEvent> orderStatusEvents;

    @NonNull
    private List<OrderItem> orderItems;

    public Order() {

        this.guid = UUID.randomUUID().toString();
    }

    public Order(List<OrderStatusEvent> orderStatusEvents, List<OrderItem> orderItems) {

        this.guid = UUID.randomUUID().toString();
        this.orderStatusEvents = orderStatusEvents;
        this.orderItems = orderItems;
    }
}
