package com.storefront.controller;

import com.storefront.Utility;
import com.storefront.kafka.Sender;
import com.storefront.model.*;
import com.storefront.respository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerRepository customerRepository;

    private MongoTemplate mongoTemplate;

    private Sender sender;


    @Autowired
    public CustomerController(CustomerRepository customerRepository, MongoTemplate mongoTemplate, Sender sender) {
        this.customerRepository = customerRepository;
        this.mongoTemplate = mongoTemplate;
        this.sender = sender;
    }

    @RequestMapping(path = "/samples", method = RequestMethod.GET)
    public ResponseEntity<String> sampleOrders() {

        List<Customer> customerList = customerRepository.findAll();

        for (Customer customer : customerList) {
            customer.setOrders(Utility.createSampleOrderHistory());
        }

        customerRepository.saveAll(customerList);

        return new ResponseEntity("Orders to customer order history", HttpStatus.OK);
    }

    @RequestMapping(path = "/sample", method = RequestMethod.GET)
    public ResponseEntity<String> sampleOrder() {

        List<Customer> customerList = customerRepository.findAll();

        for (Customer customer : customerList) {
            List<Order> orderList = customer.getOrders();
            orderList.add(Utility.createSampleOrder());
            customer.setOrders(orderList);
        }

        customerRepository.saveAll(customerList);

        return new ResponseEntity("New 'Pending' order added to customer order history", HttpStatus.OK);
    }

    @RequestMapping(path = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, List<Customer>>> customerSummary() {

        List<Customer> customerList = customerRepository.findAll();
        return new ResponseEntity<>(Collections.singletonMap("customers", customerList), HttpStatus.OK);
    }

    @RequestMapping(path = "/fulfill", method = RequestMethod.GET)
    public ResponseEntity<String> fulfillSampleOrder() {
        List<Customer> customerList = customerRepository.findAll();
//                mongoTemplate.find(new Query(where("order.status").is(Status.PENDING)), Customer.class);

        for (Customer customer : customerList) {
            FulfillmentRequest fulfillmentRequest = new FulfillmentRequest();
            fulfillmentRequest.setName(customer.getName());
            fulfillmentRequest.setContact(customer.getContact());

            Address shippingAddress = customer.getAddresses()
                    .stream()
                    .filter(o -> o.getType().equals(AddressType.SHIPPING))
                    .findFirst()
                    .orElse(null);

            fulfillmentRequest.setAddress(shippingAddress);

            Order pendingOrder = customer.getOrders()
                    .stream()
                    .filter(o -> o.getStatus().equals(Status.PENDING))
                    .findFirst()
                    .orElse(null);

            fulfillmentRequest.setOrder(pendingOrder);

            sender.send(fulfillmentRequest);
        }

        return new ResponseEntity("All pending orders sent for fulfillment", HttpStatus.OK);
    }

}
