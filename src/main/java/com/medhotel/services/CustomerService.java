package com.medhotel.services;

import com.medhotel.dao.CustomerRepository;
import com.medhotel.dao.CustomerRepositoryImpl;
import com.medhotel.models.Customer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerService {
        private static CustomerService instance;
        private CustomerRepositoryImpl customerRepository;

        // Private constructor for Singleton
        private CustomerService() {
         customerRepository = new CustomerRepositoryImpl();
        }

        // Static method to get the singleton instance
        public static CustomerService getInstance() {
            if (instance == null) {
                synchronized (CustomerService.class) {
                    if (instance == null) {
                        instance = new CustomerService();
                    }
                }
            }
            return instance;
        }

    public void addCustomer(Customer customer) {
        customerRepository.addCustomer(customer);
    }

        // Method to get all customers as a list
        public List<Customer> getAllCustomers() {
            return customerRepository.getAllCustomers();
        }

        // New method to get all customers as a map
        public Map<Integer, Customer> getAllCustomersMap() {
            return customerRepository.getAllCustomers().stream()
                    .collect(Collectors.toMap(Customer::getCustomerId, customer -> customer));
        }
}
