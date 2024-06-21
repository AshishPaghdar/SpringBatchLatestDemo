package com.springbatchprocessing.itemwriter;

import com.springbatchprocessing.entity.Customer;
import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class CustomerItemWriter implements ItemWriter<Customer> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerItemWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean tableExists = false;


    private void createTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS customer (" +
                "id INT," +
                "first_name VARCHAR(255)," +
                "last_name VARCHAR(255)," +
                "email VARCHAR(255)," +
                "profession VARCHAR(255))";
        jdbcTemplate.execute(createTableSql);
    }

    @Override
    public void write(@NonNull Chunk<? extends Customer> chunk) {
        String currentResource = null;

        if (!chunk.isEmpty()) {
            currentResource = chunk.getItems().getFirst().getResourceFileName();
        }

        if (currentResource != null) {
            log.info("Processing Customer Item from : {}", currentResource);
        }
        if (!tableExists) {
            createTable();
            tableExists = true;
        }
        long startTime = System.nanoTime();

        for (Customer customer : chunk) {
            String sql = "INSERT INTO customer (id, first_name, last_name, email, profession) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getProfession());
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println("Execution time for insert command: " + duration + " milliseconds");
    }

}
