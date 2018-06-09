package com.storefront.utilities;

import com.storefront.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class SampleData {

    public SampleData() {

    }

    public static List<Order> createSampleOrderHistory() {


        // Random Order #1
        List<OrderItem> orderItems = getRandomOrderItems();
        List<Order> orderList = new ArrayList<>();
        List<OrderStatusEvent> orderStatusEventList = new ArrayList<>();
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.CREATED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.REJECTED, "Primary credit card expired"));
        orderList.add(new Order(orderStatusEventList, orderItems));

        // Random Order #2
        orderItems = getRandomOrderItems();
        orderStatusEventList = new ArrayList<>();
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.CREATED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.APPROVED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.PROCESSING));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.SHIPPED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.IN_TRANSIT));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.RECEIVED));
        orderList.add(new Order(orderStatusEventList, orderItems));

        // Random Order #3
        orderItems = getRandomOrderItems();
        orderStatusEventList = new ArrayList<>();
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.CREATED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.APPROVED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.PROCESSING));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.ON_HOLD, "Items out of stock"));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.CANCELLED, "Ordered alternative items"));
        orderList.add(new Order(orderStatusEventList, orderItems));

        // Random Order #4
        orderItems = getRandomOrderItems();
        orderStatusEventList = new ArrayList<>();
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.CREATED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.APPROVED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.PROCESSING));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.SHIPPED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.IN_TRANSIT));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.RECEIVED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.RETURNED, "Items damaged during shipping"));
        orderList.add(new Order(orderStatusEventList, orderItems));

        // Random Order #5 Pending fulfillment...
        orderItems = getRandomOrderItems();
        orderStatusEventList = new ArrayList<>();
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.CREATED));
        orderStatusEventList.add(new OrderStatusEvent(OrderStatusType.APPROVED));
        orderList.add(new Order(orderStatusEventList, orderItems));

        return orderList;
    }

    private static List<OrderItem> getRandomOrderItems() {

        List<Product> productList = createSampleProducts();
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < getRandomProductQuantity(); i++) {
            orderItems.add(new OrderItem(productList.get(getRandomProductListIndex()), getRandomProductQuantity()));
        }
        return orderItems;
    }

    private static List<Product> createSampleProducts() {

        List<Product> productList = new ArrayList<>();
        productList.add(new Product("b5efd4a0-4eb9-4ad0-bc9e-2f5542cbe897", "Blue Widget", "Brilliant Blue Widget", new BigDecimal("1.99")));
        productList.add(new Product("d01fde07-7c24-49c5-a5f1-bc2ce1f14c48", "Red Widget", "Reliable Red Widget", new BigDecimal("3.99")));
        productList.add(new Product("a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d", "Yellow Widget", "Amazing Yellow Widget", new BigDecimal("5.99")));
        productList.add(new Product("4efe33a1-722d-48c8-af8e-7879edcad2fa", "Purple Widget", "Pretty Purple Widget", new BigDecimal("7.99")));
        productList.add(new Product("f3b9bdce-10d8-4c22-9861-27149879b3c1", "Orange Widget", "Opulent Orange Widget", new BigDecimal("9.99")));
        productList.add(new Product("7f3c9c22-3c0a-47a5-9a92-2bd2e23f6e37", "Green Widget", "Gorgeous Green Widget", new BigDecimal("11.99")));
        productList.add(new Product("b506b962-fcfa-4ad6-a955-8859797edf16", "Black Widget", "Beautiful Black Widget", new BigDecimal("13.99")));
        productList.add(new Product("c8810c1d-b0ea-486b-acfa-7724bb70f5e6", "White Widget", "Wonderful White Widget", new BigDecimal("15.99")));

        return productList;
    }

    private static int getRandomProductListIndex() {

        // 0 - 7
        Random rand = new Random();
        return rand.nextInt(7);
    }

    private static int getRandomProductQuantity() {

        // 1 - 5
        int min = 1;
        int max = 5;
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}