package org.cedac.batchtest.processor;

import lombok.AllArgsConstructor;
import org.cedac.batchtest.entity.Customer;
import org.cedac.batchtest.entity.dto.CustomerDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

@Component
@AllArgsConstructor
public class CsvItemProcessor implements ItemProcessor<CustomerDTO, Customer> {

    private final Validator validator;

    @Override
    public Customer process(CustomerDTO customer) throws Exception {

            // Validate the entity fields using the custom validator

            Set<ConstraintViolation<CustomerDTO>> violations = validator.validate(customer);
            if (!violations.isEmpty()) {
                // Handle validation errors, e.g., log the errors, throw an exception, etc.
                for (ConstraintViolation<CustomerDTO> violation : violations) {
                    System.out.println("Validation error: " + violation.getPropertyPath() + " - " + violation.getMessage());
                }
                return null; // Skip invalid item
            }

        // If the item is invalid, return null to indicate filtering it out
        Customer customer1 = new Customer();
        customer1.setFirstName(customer.getFirstName());
        customer1.setLastName(customer.getLastName());
        customer1.setEmail(customer.getEmail());
        return customer1;
    }
}
