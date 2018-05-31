package com.storefront.kafka;

import com.storefront.model.Customer;
import com.storefront.model.FulfillmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class Sender {

    @Value("${spring.kafka.topic.orders-order}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, FulfillmentRequest> kafkaTemplate;

    public void send(FulfillmentRequest payload) {
        log.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}