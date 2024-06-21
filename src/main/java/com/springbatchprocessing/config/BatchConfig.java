package com.springbatchprocessing.config;

import com.springbatchprocessing.entity.Customer;
import com.springbatchprocessing.itemprocessor.CustomerItemProcessor;
import com.springbatchprocessing.itemwriter.CustomerItemWriter;
import com.springbatchprocessing.tasklet.DataCleansingTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
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
    private final MultiResourceItemReader<Customer> customerItemReader;

    private Resource currentResource;

    public BatchConfig(DataCleansingTasklet dataCleansingTasklet, JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource, MultiResourceItemReader<Customer> customerItemReader) {
        this.dataCleansingTasklet = dataCleansingTasklet;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.customerItemReader = customerItemReader;
    }

    @Bean
    public Job customerJob() {
        return new JobBuilder("customer-job", jobRepository)
                .start(customerStep())
                .next(dataCleansingStep())
                .listener(this)
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
                .reader(customerItemReader)
                .processor(new CustomerItemProcessor())
                .writer(new CustomerItemWriter(new JdbcTemplate(dataSource)))
                .build();
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.currentResource= stepExecution.getExecutionContext().containsKey("currentResource") ? (Resource) stepExecution.getExecutionContext().get("currentResource") : null;
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        if (currentResource != null) {
            System.out.println("Currently processing resource: " + currentResource.getFilename());
        }
    }
}
