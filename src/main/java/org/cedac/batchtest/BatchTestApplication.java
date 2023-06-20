package org.cedac.batchtest;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication()
@AllArgsConstructor
@Configuration
public class BatchTestApplication implements CommandLineRunner {

    private JobLauncher jobLauncher;

    private Job importCsvJob;
    public static void main(String[] args) {
        SpringApplication.run(BatchTestApplication.class, args);
    }


    @Override
    public void run(String...args) throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        try{
            jobLauncher.run(importCsvJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException| JobRestartException| JobInstanceAlreadyCompleteException | JobParametersInvalidException e){
            throw new RuntimeException(e);
        }
    }
}
