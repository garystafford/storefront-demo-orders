package com.storefront.kafka;

import com.storefront.model.FulfillmentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class Sender {

    @Autowired
    private KafkaTemplate<String, FulfillmentEvent> kafkaTemplate;

    public void send(String topic, FulfillmentEvent payload) {

        log.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}