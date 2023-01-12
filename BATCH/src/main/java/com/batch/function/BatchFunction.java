package com.batch.function;

import java.io.File;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.cglib.core.internal.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.batch.service.BatchService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchFunction implements Function<File, ResponseEntity<BatchStatus>> {

	private BatchService batchService;
	
	@Override
	public ResponseEntity<BatchStatus> apply(File file) {
		BatchStatus status = null;
		try {
			status = batchService.batch(file);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
		}
		return new ResponseEntity<BatchStatus>(status, HttpStatus.OK);
	}
}