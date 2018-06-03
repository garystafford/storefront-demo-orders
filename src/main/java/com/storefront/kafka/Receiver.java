package com.storefront.kafka;

import com.storefront.model.CustomerOrders;
import com.storefront.respository.CustomerOrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class Receiver {

    @Autowired
    private CustomerOrdersRepository customerOrdersRepository;

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {

        return latch;
    }

    @KafkaListener(topics = "${spring.kafka.topic.accounts-customerOrders}")
    public void receive(CustomerOrders customerOrders) {

        log.info("received payload='{}'", customerOrders.toString());
        latch.countDown();

        customerOrdersRepository.save(customerOrders);
    }
}