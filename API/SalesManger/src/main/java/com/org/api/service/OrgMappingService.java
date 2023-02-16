package com.org.api.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.org.api.document.OrgDocument;
import com.org.api.repository.OrgDocumentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;

@Service
@RequiredArgsConstructor
public class OrgMappingService {

	private final OrgDocumentRepository repository;

	private final AmazonS3 amazonS3Client;

	private final LambdaClient lambdaClient;

	@Value("${cloud.aws.s3.bucket}")
	public String bucket;

	public Flux<DataBuffer> mapping(String fileName, Flux<DataBuffer> flux) {
		return flux.doOnNext(d -> {
			ObjectMetadata objectMetaData = new ObjectMetadata();
			objectMetaData.setContentType("xlsx");
			objectMetaData.setContentLength(d.readableByteCount());

			try (InputStream inputStream = d.asInputStream()) {
				amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetaData)
						.withCannedAcl(CannedAccessControlList.PublicRead));
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
			}
		});

	}

	public Mono<String> getStatus() {
		GetFunctionRequest functionRequest = GetFunctionRequest.builder().functionName("OrganizationalStructureBatch").build();

		GetFunctionResponse response = lambdaClient.getFunction(functionRequest);
		return Mono.just(response.configuration().stateAsString());
	}

	public Mono<Page<OrgDocument>> findAll(Pageable pageable) {
		return repository.findAllBy(pageable).collectList().zipWith(repository.count())
				.flatMap(t -> Mono.just(new PageImpl<>(t.getT1(), pageable, t.getT2())));
	}

	public Mono<byte[]> excel() throws IOException {
		S3Object o = amazonS3Client.getObject(new GetObjectRequest(bucket, "조직구조.xlsx"));
		S3ObjectInputStream objectInputStream = o.getObjectContent();

		return Mono.just(objectInputStream.readAllBytes());
	}

}
