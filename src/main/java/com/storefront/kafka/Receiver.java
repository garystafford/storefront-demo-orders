package com.storefront.kafka;

import com.storefront.model.CustomerOrders;
import com.storefront.model.OrderStatusChangeEvent;
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
    public void receiveOrderStatusChangeEvents(OrderStatusChangeEvent orderStatusChangeEvent) {

        log.info("received payload='{}'", orderStatusChangeEvent);
        latch.countDown();

        Criteria criteria = Criteria.where("orders.guid")
                .is(orderStatusChangeEvent.getGuid());
        Query query = Query.query(criteria);

        Update update = new Update();
        update.addToSet("orders.$.orderStatusEvents", orderStatusChangeEvent.getOrderStatusEvent());
        mongoTemplate.updateFirst(query, update, "customer.orders");
    }
}