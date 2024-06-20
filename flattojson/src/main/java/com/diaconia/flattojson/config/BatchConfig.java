package com.diaconia.flattojson.config;

import com.diaconia.flattojson.batch.CustomVSAMFieldSetMapper;
import com.diaconia.flattojson.batch.FixedLengthTokenizerFactory;
import com.diaconia.flattojson.batch.JobCompletionNotificationListener;
import com.diaconia.flattojson.batch.MultipleFileItemWriterClassifier;
import com.diaconia.flattojson.model.DataElement;
import com.diaconia.flattojson.model.VSAMRecord;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
//with Spring boot 3.0, the @EnableBatchProcessing is discouraged
//Also, JobBuilderFactory and StepBuilderFactory are deprecated and it is recommended to use JobBuilder and StepBuilder classes with the name of the job or step builder.
//https://www.baeldung.com/spring-boot-spring-batch
@EnableTransactionManagement
public class BatchConfig {
    String filePath = "C:\\temp\\300k.txt";



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
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,ItemWriter<VSAMRecord> writer
            , FlatFileItemReader reader) throws Exception {

        return new StepBuilder("flatToJSON", jobRepository)
                .<VSAMRecord, String>chunk(2_000_000, transactionManager)
                .reader(reader)
                .writer(writer)
                .faultTolerant()
                .skipLimit(Integer.MAX_VALUE)
                .skip(Exception.class)
                .build();
    }


    @Bean
    public FlatFileItemReader reader(LineMapper<VSAMRecord> lineMapper) {
        return new FlatFileItemReaderBuilder().name("Flat File Reader")
                .linesToSkip(0)
                .resource(new FileSystemResource(filePath))
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
    public LineTokenizer tokenizer(List<DataElement> dataElementList) {
        return FixedLengthTokenizerFactory.createFixedLengthTokenizer(dataElementList);
    }

    @Bean
    public List<DataElement> dataElementList() {
        return List.of(
                new DataElement("taxPayerIdentificationNumber", "int", 0, 4)
                ,new DataElement("trans", "int", 4, 4)
                ,new DataElement("reasonCode3", "int", 8, 4)
                ,new DataElement("xrefTaxPayerIdentificationNumber", "int", 12, 4)
        );
    }
    /*
    @Bean
    public JsonFileItemWriter<VSAMRecord> jsonFileItemWriter() {
        return new JsonFileItemWriterBuilder<VSAMRecord>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("vsam.json"))
                .name("VSAM Record JsonFileItemWriter")
                .build();
    }



     */
    @Bean
    public ClassifierCompositeItemWriter<VSAMRecord> jsonFileItemWriter() {
        ClassifierCompositeItemWriter<VSAMRecord> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(new MultipleFileItemWriterClassifier());
        return compositeItemWriter;
    }

}
