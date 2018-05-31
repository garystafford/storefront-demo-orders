package com.storefront.controller;

import com.storefront.Utility;
import com.storefront.kafka.Sender;
import com.storefront.model.*;
import com.storefront.respository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @RequestMapping(path = "/sample", method = RequestMethod.GET)
    public ResponseEntity<String> sampleData() {

        List<Customer> customerList = customerRepository.findAll();

        for (Customer customer : customerList) {
            customer.setOrders(Utility.createSampleOrderHistory());
        }

        customerRepository.saveAll(customerList);

        return new ResponseEntity("Customer order history added", HttpStatus.OK);
    }

    @RequestMapping(path = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, List<Customer>>> customerSummary() {

        List<Customer> customerList = customerRepository.findAll();
        return new ResponseEntity<>(Collections.singletonMap("customers", customerList), HttpStatus.OK);
    }

    @RequestMapping(path = "/fulfill-order", method = RequestMethod.GET)
    public ResponseEntity<String> fulfillSampleOrder() {
        List<Customer> customerList =
                mongoTemplate.find(new Query(where("order.status").is(Status.PENDING)),Customer.class);

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

        return new ResponseEntity("Order sent for fulfillment", HttpStatus.OK);
    }

}
