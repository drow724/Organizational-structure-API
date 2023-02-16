package com.org.api.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.org.api.document.OrgDocument;

import reactor.core.publisher.Flux;

public interface OrgDocumentRepository extends ReactiveMongoRepository<OrgDocument, Long> {
	Flux<OrgDocument> findAllBy(Pageable pageable);
}
