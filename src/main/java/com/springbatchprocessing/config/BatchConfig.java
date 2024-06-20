package com.springbatchprocessing.config;

import com.springbatchprocessing.entity.Customer;
import com.springbatchprocessing.itemprocessor.CustomerItemProcessor;
import com.springbatchprocessing.itemreader.CustomerItemReader;
import com.springbatchprocessing.itemwriter.CustomerItemWriter;
import com.springbatchprocessing.tasklet.DataCleansingTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
@Slf4j
public class BatchConfig {

    private final DataCleansingTasklet dataCleansingTasklet;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public BatchConfig(DataCleansingTasklet dataCleansingTasklet, JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.dataCleansingTasklet = dataCleansingTasklet;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job customerJob() {
        return new JobBuilder("customer-job", jobRepository)
                .start(customerStep())
                .next(dataCleansingStep())
                .build();
    }

    @Bean
    public Step dataCleansingStep() {
        return new StepBuilder("dataCleansingStep", jobRepository)
                .tasklet(dataCleansingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step customerStep() {
        return new StepBuilder("customerStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(new CustomerItemReader().customerFlatFileItemReader())
                .processor(new CustomerItemProcessor())
                .writer(new CustomerItemWriter(new JdbcTemplate(dataSource)))
                .build();
    }
}
