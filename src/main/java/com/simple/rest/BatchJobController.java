package com.simple.rest;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
public class BatchJobController {

    private final JobLauncher jobLauncher;

    private final Job importPeopleJob;
    private final Job exportPeopleJob;

    public BatchJobController(JobLauncher jobLauncher, @Qualifier("importPeopleJob") Job importPeopleJob, @Qualifier("exportPeopleJob") Job exportPeopleJob) {
        this.jobLauncher = jobLauncher;
        this.importPeopleJob = importPeopleJob;
        this.exportPeopleJob = exportPeopleJob;
    }

    @PostMapping("/run/import-people")
    public BatchStatus runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        // TODO 1.
        JobParameters jobParameters = new JobParametersBuilder()
                .toJobParameters();

        return jobLauncher.run(importPeopleJob, jobParameters).getStatus();
    }

    @PostMapping("/run/export-people")
    public BatchStatus runExportPeopleJobJob(@RequestBody Params params) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("fileName", params.getFileName())
                .addString("timestamp", params.getTimestamp())
                .toJobParameters();

        return jobLauncher.run(exportPeopleJob, jobParameters).getStatus();
    }
}
