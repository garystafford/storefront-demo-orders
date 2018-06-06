package com.storefront.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storefront.model.CustomerOrders;
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

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class Receiver {

    @Autowired
    private CustomerOrdersRepository customerOrdersRepository;

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

        Criteria elementMatchCriteria = Criteria.where("orders.order")
                .elemMatch(Criteria.where("guid").is(orderStatusEventChange.getGuid()));
        Query query = Query.query(elementMatchCriteria);
        Update update = new Update();
        update.addToSet("orders.order.orderStatusEvents", orderStatusEventChange);
        mongoTemplate.updateFirst(query, update, CustomerOrders.class);
    }
}