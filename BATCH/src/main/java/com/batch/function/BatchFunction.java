package com.batch.function;

import org.springframework.batch.core.BatchStatus;
import org.springframework.cglib.core.internal.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.batch.service.BatchService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchFunction implements Function<MultipartFile, ResponseEntity<BatchStatus>> {

	private final BatchService batchService;
	
	@Override
	public ResponseEntity<BatchStatus> apply(MultipartFile file) {
		BatchStatus status = batchService.batch(file);
		return new ResponseEntity<BatchStatus>(status, HttpStatus.OK);
	}
}