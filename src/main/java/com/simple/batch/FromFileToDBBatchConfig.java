package com.simple.batch;

import com.simple.dto.PersonDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class FromFileToDBBatchConfig {

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    public FromFileToDBBatchConfig(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public FlatFileItemReader<PersonDto> fromFileReader() {
        return new FlatFileItemReaderBuilder().name("fromFileReader")
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(fieldSet -> PersonDto.builder().firstName(fieldSet.readString(0)).lastName(fieldSet.readString(1)).build())
                .resource(new FileSystemResource("input_file"))
                .build();
    }

    @Bean("fromFileToDBStep")
    public Step readFileStep() {
        return stepBuilderFactory
                .get("fromFileToDBStep")
                .chunk(2).reader(fromFileReader()).writer(list -> list.forEach(item -> System.out.println(item))).build();
    }

    @Bean("importPeopleJob")
    public Job readFileJob() {
        return jobBuilderFactory.get("readFileJob").start(readFileStep()).build();
    }
}
