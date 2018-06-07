package com.storefront.kafka;

import com.storefront.model.CustomerOrders;
import com.storefront.model.Order;
import com.storefront.model.OrderStatusEventChange;
import com.storefront.respository.CustomerOrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class Receiver {

    @Autowired
    private CustomerOrdersRepository customerOrdersRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {

        return latch;
    }

    @KafkaListener(topics = "${spring.kafka.topic.accounts-customer}")
    public void receiveCustomerOrder(CustomerOrders customerOrders) {

        log.info("received payload='{}'", customerOrders);
        latch.countDown();
        customerOrdersRepository.save(customerOrders);
    }

    @KafkaListener(topics = "${spring.kafka.topic.fulfillment-order}")
    public void receiveOrderStatusEvents(OrderStatusEventChange orderStatusEventChange) {

        log.info("received payload='{}'", orderStatusEventChange);
        latch.countDown();

        Criteria criteria = Criteria.where("orders.guid")
                .is(orderStatusEventChange.getGuid());
        Query query = Query.query(criteria);
//        CustomerOrders customerOrders = mongoTemplate.findOne(query, CustomerOrders.class, "customer.orders");
//        log.info(customerOrders.toString());

        Update update = new Update();
        update.addToSet("orders.$.orderStatusEvents", orderStatusEventChange.getOrderStatusEvent());
        mongoTemplate.updateFirst(query, update, "customer.orders");
    }
}