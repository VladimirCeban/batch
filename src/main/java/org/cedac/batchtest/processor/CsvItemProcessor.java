package org.cedac.batchtest.processor;

import org.cedac.batchtest.entity.Customer;
import org.cedac.batchtest.entity.dto.CustomerDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CsvItemProcessor implements ItemProcessor<CustomerDTO, Customer> {

    @Override
    public Customer process(CustomerDTO customer) throws Exception {
        // Perform your validation logic using the existing Validator
//        Validator validator = new Validator();
//        boolean isValid = validator.validate(item);
//
//        // If the item is valid, return it
//        if (isValid) {
//            return item;
//        }

        // If the item is invalid, return null to indicate filtering it out
        Customer customer1 = new Customer();
        customer1.setId(customer.getId());
        customer1.setFirstName(customer.getFirstName());
        customer1.setLastName(customer.getLastName());
        customer1.setEmail(customer.getEmail());
        return customer1;
    }
}
