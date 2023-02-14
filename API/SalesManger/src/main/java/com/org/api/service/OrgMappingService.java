package com.org.api.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.org.api.document.OrgDocument;
import com.org.api.repository.OrgDocumentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class OrgMappingService {

	private final OrgDocumentRepository repository;

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	public String bucket;

	public Flux<DataBuffer> mapping(String fileName, Flux<DataBuffer> flux) {
		return flux.doOnNext(d -> {
			ObjectMetadata objectMetaData = new ObjectMetadata();
			objectMetaData.setContentType("xlsx");
			objectMetaData.setContentLength(d.readableByteCount());

			try(InputStream inputStream = d.asInputStream()) {
				amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch(IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
		});

	}

	public Flux<OrgDocument> findAll(Pageable pageable) {
		return repository.findAll(pageable.getSort()).skip(pageable.getOffset()).take(pageable.getPageSize());
	}

}
