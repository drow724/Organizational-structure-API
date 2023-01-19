package com.batch.function;

import java.io.ByteArrayInputStream;

import org.springframework.batch.core.BatchStatus;
import org.springframework.cglib.core.internal.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.batch.dto.FileDTO;
import com.batch.service.BatchService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchFunction implements Function<FileDTO, ResponseEntity<BatchStatus>> {

	private final BatchService batchService;

	@Override
	public ResponseEntity<BatchStatus> apply(FileDTO fileDTO) {
		return new ResponseEntity<BatchStatus>(batchService.batch(new ByteArrayInputStream(fileDTO.getFile())),
				HttpStatus.OK);
	}
}