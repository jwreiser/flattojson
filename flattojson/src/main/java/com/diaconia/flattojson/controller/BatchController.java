package com.diaconia.flattojson.controller;

import com.diaconia.flattojson.batch.BatchConstants;
import com.diaconia.flattojson.config.Constants;
import com.diaconia.flattojson.model.BatchInput;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostMapping("/batch")
    public void processBatch(@RequestBody BatchInput batchInput) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        System.setProperty(Constants.PROPERTY_FILE_PATH, batchInput.getFilePath());
        jobLauncher.run(job, new JobParametersBuilder().addString(BatchConstants.FILE_PATH, batchInput.getFilePath()).toJobParameters());
    }
}
