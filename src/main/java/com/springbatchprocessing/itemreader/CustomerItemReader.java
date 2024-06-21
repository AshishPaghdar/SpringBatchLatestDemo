package com.springbatchprocessing.itemreader;

import com.springbatchprocessing.entity.Customer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class CustomerItemReader {

    @Value("classpath:csv/myrecords_*.csv")
    private Resource[] resources;


    @Bean
    public MultiResourceItemReader<Customer> reader(FlatFileItemReader<Customer> customerFlatFileItemReader) {
        MultiResourceItemReader<Customer> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);
        reader.setDelegate(customerFlatFileItemReader);
        return reader;
    }

    @Bean
    public FlatFileItemReader<Customer> customerFlatFileItemReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        //        reader.setResource(new ClassPathResource("myrecords_1.csv"));
        reader.setName("csv-reader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(customerLineMapper());
        return reader;
    }

    private LineMapper<Customer> customerLineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstname", "lastname", "email", "profession");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
}
