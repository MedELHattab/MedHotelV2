package com.medhotel.dao;

import com.medhotel.models.Customer;

import java.util.List;

public interface CustomerRepository {
    void addCustomer(Customer customer);
    public List<Customer> getAllCustomers() ;
}

