package com.springbatchprocessing.config;

import com.springbatchprocessing.entity.Customer;
import com.springbatchprocessing.itemprocessor.CustomerItemProcessor;
import com.springbatchprocessing.itemreader.CustomerItemReader;
import com.springbatchprocessing.itemwriter.CustomerItemWriter;
import com.springbatchprocessing.tasklet.DataCleansingTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final DataCleansingTasklet dataCleansingTasklet;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    public BatchConfig(DataCleansingTasklet dataCleansingTasklet, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.dataCleansingTasklet = dataCleansingTasklet;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public Job customerJob(){
        return jobBuilderFactory.get("customer-job")
                .start(customerStep())
                .next(dataCleansingStep())
                .build();
    }

    @Bean
    public Step dataCleansingStep(){
        return stepBuilderFactory.get("dataCleansingTasklet")
                .tasklet(dataCleansingTasklet)
                .build();
    }

    @Bean
    public Step customerStep(){
        return stepBuilderFactory.get("customer-step")
                .<Customer,Customer>chunk(100)
                .reader(new CustomerItemReader().customerFlatFileItemReader())
                .processor(new CustomerItemProcessor())
                .writer(new CustomerItemWriter(new JdbcTemplate(dataSource)))
                .build();
    }
}
