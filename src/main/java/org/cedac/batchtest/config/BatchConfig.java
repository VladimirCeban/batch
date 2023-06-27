package org.cedac.batchtest.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.cedac.batchtest.entity.Customer;
import org.cedac.batchtest.entity.dto.CustomerDTO;
import org.cedac.batchtest.processor.CsvItemProcessor;
import org.cedac.batchtest.repository.CustomerRepository;
import org.cedac.batchtest.tasklet.MoveFileTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;



import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@AllArgsConstructor
public class BatchConfig extends BatchAutoConfiguration{

    private CustomerRepository customerRepository;

    private PlatformTransactionManager transactionManager;


    @Bean
    public FlatFileItemReader<CustomerDTO> customerReader() {
        FlatFileItemReader<CustomerDTO> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("data.csv"));
        reader.setName("csvReader");
        reader.setLinesToSkip(1); // Пропустить заголовок CSV-файла

        reader.setLineMapper(lineMapper());

        return reader;
    }

    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    private LineMapper<CustomerDTO> lineMapper() {
        DefaultLineMapper<CustomerDTO> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames("id", "firstName", "lastName", "email");

        BeanWrapperFieldSetMapper<CustomerDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CustomerDTO.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public Job importCsvJob(Step importCsvStep, JobRepository repository, Step moveFileStep) {
        return new JobBuilder("importCsvJob", repository)
                .flow(importCsvStep)
                .next(moveFileStep)
                .end()
                .build();
    }

    @Bean
    public Step importCsvStep(JobRepository repository,
    /*ItemReader<CustomerDTO> csvItemReader*/
            FlatFileItemReader<CustomerDTO> customerReader,
                              CsvItemProcessor processor,
                              RepositoryItemWriter<Customer> writer) {
        return new StepBuilder("importCsvStep", repository)
                .<CustomerDTO, Customer>chunk(10, transactionManager)
                .reader(customerReader)
                .processor(processor)
                .writer(writer)
                //.tasklet(importTasklet(), transactionManager)
                .build();
    }


/*    @Bean
    public ItemReader<CustomerDTO> csvItemReader() {
        FlatFileItemReader<CustomerDTO> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("data.csv")); // Set the path to your CSV file
        reader.setLinesToSkip(1); // Skip the header line
        reader.setName("csvReader");
        reader.setLineMapper(new DefaultLineMapper<CustomerDTO>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("id","firstName", "lastName", "email"); // Set the names of the columns in the CSV file
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(CustomerDTO.class);
            }});
        }});
        return reader;
    }*/

    // Define your custom processor bean  CsvItemProcessor


    @Bean
    public CsvItemProcessor processor(Validator validator ){
        return new CsvItemProcessor(validator); // Replace with your custom processor implementation
    }

    // Define your custom writer bean  csvItemWriter
    @Bean
    public RepositoryItemWriter<Customer> writer() {

        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer; // Replace with your custom writer implementation
    }

    @Bean
    public Step moveFileStep(MoveFileTasklet moveFileTasklet, JobRepository repository) {
        return new StepBuilder("moveFileStep", repository)
                .tasklet(moveFileTasklet, transactionManager)
                .build();
    }

}