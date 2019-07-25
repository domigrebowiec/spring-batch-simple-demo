package com.simple.batch;

import java.io.File;
import java.time.LocalDateTime;
import javax.persistence.EntityManagerFactory;

import com.simple.domain.Person;
import com.simple.dto.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
public class FromDBToFileBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

    public FromDBToFileBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public JpaPagingItemReader<Person> reader() {
        return new JpaPagingItemReaderBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .name("jpaPagingItemReaderBuilder")
                .queryString("select p from People p order by p.person_id")
                .build();
    }

    @Bean
    public ItemProcessor<Person, PersonDto> processor() {
        return person -> PersonDto.builder()
                .firstName(person.getFirst_name().toUpperCase())
                .lastName(person.getLast_name().toUpperCase())
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<PersonDto> writer(@Value("#{jobParameters[fileName]}") String fileName) {
        return new FlatFileItemWriterBuilder<PersonDto>()
                .lineAggregator(personDto -> {
                    log.debug("writing " + personDto);
                    // TODO 3.
                    if ("Aleksandra".toUpperCase().equals(personDto.getFirstName())) {
                        throw new IllegalStateException("wrong first name, stop processing");
                    }

                    return personDto.getFirstName() + " - " + personDto.getLastName();})
                .resource(new FileSystemResource(new File(fileName)))
                .name("flatFileItemWriter")
                // TODO 1.
                .headerCallback(writer -> writer.write("time " + LocalDateTime.now()))
                .build();
    }

    @Bean(name = "exportPeopleJob")
    public Job exportPeople(@Qualifier("peopleFromDBToFileStep") Step step1) {
        return jobBuilderFactory.get("exportPeople")
                .flow(step1)
                .end()
                .build();
    }

    @Bean(name = "peopleFromDBToFileStep")
    public Step step1(JpaPagingItemReader<Person> reader,
                      ItemProcessor<Person, PersonDto> processor,
                      FlatFileItemWriter<PersonDto> writer) {
        return stepBuilderFactory.get("peopleFromDBToFile")
                .<Person, PersonDto>chunk(2)// TODO 3.
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
