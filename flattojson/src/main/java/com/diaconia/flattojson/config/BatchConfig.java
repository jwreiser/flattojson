package com.diaconia.flattojson.config;

import com.diaconia.flattojson.batch.CustomVSAMFieldSetMapper;
import com.diaconia.flattojson.batch.JobCompletionNotificationListener;
import com.diaconia.flattojson.model.VSAMRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
//with Spring boot 3.0, the @EnableBatchProcessing is discouraged
//Also, JobBuilderFactory and StepBuilderFactory are deprecated and it is recommended to use JobBuilder and StepBuilder classes with the name of the job or step builder.
//https://www.baeldung.com/spring-boot-spring-batch
@EnableTransactionManagement
public class BatchConfig {
    String filePath = "C:\\temp\\vsam.csv";



    @Bean
    /**
     * @param jobRepository persists metadata about batch jobs
     * @param listener
     * @param step1
     */
    public Job flatToJSONJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step) {


        return new JobBuilder("Flat File to JSON", jobRepository)
//                .incrementer(new RunIdIncrementer())
                .listener(listener)
//                .validator(validator())
                .flow(step)
                .end()// only for flow not for start
                .build();
    }


    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,ItemWriter<VSAMRecord> writer, FlatFileItemReader reader) throws Exception {

        return new StepBuilder("flatToJSON", jobRepository)
                .<VSAMRecord, String>chunk(2, transactionManager)
                .reader(reader)
                .writer(writer)
                .faultTolerant()
                .skipLimit(Integer.MAX_VALUE)
                .skip(Exception.class)
                .build();
    }


    @Bean
    public FlatFileItemReader reader(LineMapper<VSAMRecord> lineMapper) {
        return new FlatFileItemReaderBuilder().name("directoryReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .names("Directory")
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public DefaultLineMapper<VSAMRecord> lineMapper(LineTokenizer tokenizer) {
        DefaultLineMapper lineMapper = new DefaultLineMapper<VSAMRecord>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new CustomVSAMFieldSetMapper() {{
            setTargetType(VSAMRecord.class);
        }});
        return lineMapper;
    }
    @Bean
    public DelimitedLineTokenizer tokenizer() {
        var tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("taxPayerIdentificationNumber","trans","reasonCode3","xrefTaxPayerIdentificationNumber");
        return tokenizer;
    }

    @Bean
    public JsonFileItemWriter<VSAMRecord> jsonFileItemWriter() {
        return new JsonFileItemWriterBuilder<VSAMRecord>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("vsam.json"))
                .name("VSAM Record JsonFileItemWriter")
                .build();
    }

}
