package com.isariev.customerservice.repository;

import com.isariev.customerservice.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    @Query("{'userId' :  ?0}")
    List<Customer> findAllByCustomerId(String customerId);
}
