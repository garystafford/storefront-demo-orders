package com.storefront.controller;

import com.storefront.kafka.Sender;
import com.storefront.model.*;
import com.storefront.respository.CustomerOrdersRepository;
import com.storefront.utilities.SampleData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerOrdersController {

    private CustomerOrdersRepository customerOrdersRepository;

    private MongoTemplate mongoTemplate;

    @Value("${spring.kafka.topic.orders-order}")
    private String topic;

    private Sender sender;


    @Autowired
    public CustomerOrdersController(CustomerOrdersRepository customerOrdersRepository,
                                    MongoTemplate mongoTemplate,
                                    Sender sender) {

        this.customerOrdersRepository = customerOrdersRepository;
        this.mongoTemplate = mongoTemplate;
        this.sender = sender;
    }

    @RequestMapping(path = "/sample/orders", method = RequestMethod.GET)
    public ResponseEntity<String> sampleOrders() {

        List<CustomerOrders> customerOrdersList = customerOrdersRepository.findAll();

        for (CustomerOrders customerOrders : customerOrdersList) {
            customerOrders.setOrders(SampleData.createSampleOrderHistory());
        }

        customerOrdersRepository.saveAll(customerOrdersList);

        return new ResponseEntity("Sample orders added to customer orders", HttpStatus.OK);
    }

    @RequestMapping(path = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, List<CustomerOrders>>> customerSummary() {

        List<CustomerOrders> customerOrdersList = customerOrdersRepository.findAll();
        return new ResponseEntity<>(Collections.singletonMap("customers", customerOrdersList), HttpStatus.OK);
    }

    @RequestMapping(path = "/sample/fulfill", method = RequestMethod.GET)
    public ResponseEntity<String> fulfillSampleOrder() {

        Criteria elementMatchCriteria = Criteria.where("orders.orderStatusEvents")
                .size(2)
                .elemMatch(Criteria.where("orderStatusType").is(OrderStatusType.CREATED))
                .elemMatch(Criteria.where("orderStatusType").is(OrderStatusType.APPROVED));
        Query query = Query.query(elementMatchCriteria);
        List<CustomerOrders> customerOrdersList = mongoTemplate.find(query, CustomerOrders.class);

        log.info("customerOrdersList size: " + customerOrdersList.size() + '\n');


        for (CustomerOrders customerOrders : customerOrdersList) {
            FulfillmentRequestEvent fulfillmentRequestEvent = new FulfillmentRequestEvent();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            fulfillmentRequestEvent.setTimestamp(timestamp.getTime());
            fulfillmentRequestEvent.setName(customerOrders.getName());
            fulfillmentRequestEvent.setContact(customerOrders.getContact());

            Address shippingAddress = customerOrders.getAddresses()
                    .stream()
                    .filter(o -> o.getType().equals(AddressType.SHIPPING))
                    .findFirst()
                    .orElse(null);

            fulfillmentRequestEvent.setAddress(shippingAddress);

            try {
                // order where the first order status event in list is created...
                // order where the last order status event in list is approved...

                Order pendingOrder = customerOrders.getOrders()
                        .stream()
                        .filter(o -> o.getOrderStatusEvents()
                                .get(0)
                                .getOrderStatusType().equals(OrderStatusType.CREATED))
                        .filter(o -> o.getOrderStatusEvents()
                                .get(o.getOrderStatusEvents().size() - 1)
                                .getOrderStatusType().equals(OrderStatusType.APPROVED))
                        .findFirst()
                        .orElse(null);

                log.info("pending order: " + pendingOrder);

                fulfillmentRequestEvent.setOrder(pendingOrder);

                sender.send(topic, fulfillmentRequestEvent);

            } catch (NullPointerException ex) {
                log.info(ex.getMessage());
                return new ResponseEntity("No 'Approved' orders found", HttpStatus.NOT_FOUND);
            }

        }
        return new ResponseEntity("All 'Approved' orders sent for fulfillment", HttpStatus.OK);
    }
}
