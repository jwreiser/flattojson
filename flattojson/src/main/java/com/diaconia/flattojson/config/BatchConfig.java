package com.diaconia.flattojson.config;

import com.diaconia.flattojson.batch.*;
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
import org.springframework.beans.factory.annotation.Value;
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
    //The higher the chunk size the faster the processing
    /*
    performance with 10k writers
    200k: 5s
    20k: 6s
    2k: 8s
    200: 9s
    20: 11s
     */
    private static final int CHUNK_SIZE = 200_000;


    @Value("${access-method}")
    private static String accessMethod;

    @Value("${filePathProperty}")
    private static String filePathProperty;

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
                .<VSAMRecord, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .writer(writer)
                .faultTolerant()
                .skipLimit(Integer.MAX_VALUE)
                .skip(Exception.class)
                .build();
    }


    @Bean
    public FlatFileItemReader reader(LineMapper<VSAMRecord> lineMapper) {
        FlatFileItemReader reader=new ErrorNoticingFlatFileItemReader();
        reader.setName("Flat File Reader");
        reader.setLinesToSkip(0);
        String filePath;
        String filePathRead=System.getProperty(Constants.PROPERTY_FILE_PATH);
        if(filePathRead==null){
            if (accessMethod != null && accessMethod.equals("command-line")) {
                throw new IllegalArgumentException("filePath system property not set");
            }else{
                if(filePathProperty==null || filePathProperty.isEmpty()){
                    filePath="C:/temp/200k.txt";
//                    throw new IllegalArgumentException("filePath application property not set");
                }else{
                    filePath=filePathProperty;
                }
            }
        }else{
            filePath=filePathRead;
            System.out.println("filePathRead: "+filePathRead);
        }
        reader.setResource(new FileSystemResource(filePath));
        reader.setLineMapper(lineMapper);
        return reader;
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
                new DataElement("taxPayerIdentificationNumber", "int", 0, 7)
                ,new DataElement("trans", "int", 7, 7)
                ,new DataElement("reasonCode3", "int", 14, 7)
                ,new DataElement("xrefTaxPayerIdentificationNumber", "int", 21, 7)
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
