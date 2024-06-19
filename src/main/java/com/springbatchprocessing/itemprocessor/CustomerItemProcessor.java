package com.springbatchprocessing.itemprocessor;

import com.springbatchprocessing.entity.Customer;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerItemProcessor.class);

    @Override
    public Customer process(@NonNull Customer item) {

        if (!validateItem(item)) {
            log.info("Customer with id {} is not valid", item.getId());
            return null;
        } else {
            return item;
        }
    }

    private boolean validateItem(Customer item) {
        return item.getFirstName() != null && !item.getFirstName().isEmpty() &&
                item.getLastName() != null && !item.getLastName().isEmpty() &&
                item.getEmail() != null && !item.getEmail().isEmpty() &&
                item.getProfession() != null && !item.getProfession().isEmpty();
    }
}