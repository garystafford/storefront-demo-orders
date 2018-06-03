package com.storefront.respository;

import com.storefront.model.CustomerOrders;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerOrdersRepository extends MongoRepository<CustomerOrders, String> {

}