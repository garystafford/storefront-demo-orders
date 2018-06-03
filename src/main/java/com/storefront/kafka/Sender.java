package com.storefront.kafka;

import com.storefront.model.FulfillmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class Sender {

    @Autowired
    private KafkaTemplate<String, FulfillmentRequest> kafkaTemplate;

    public void send(String topic, FulfillmentRequest payload) {

        log.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}