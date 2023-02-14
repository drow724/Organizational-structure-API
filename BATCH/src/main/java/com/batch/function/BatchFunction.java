package com.batch.function;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.springframework.batch.core.BatchStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.batch.service.BatchService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchFunction implements Function<S3Event, ResponseEntity<BatchStatus>> {

	private final AmazonS3Client amazonS3Client;

	private final BatchService batchService;

	@Override
	public ResponseEntity<BatchStatus> apply(S3Event input) {
		S3EventNotificationRecord record = input.getRecords().get(0);
		String s3Key = URLDecoder.decode(record.getS3().getObject().getKey(), StandardCharsets.UTF_8);
		String s3Bucket = record.getS3().getBucket().getName();
		
		S3Object object = amazonS3Client.getObject(new GetObjectRequest(s3Bucket, s3Key));
		return new ResponseEntity<BatchStatus>(batchService.batch(object), HttpStatus.OK);
	}

}