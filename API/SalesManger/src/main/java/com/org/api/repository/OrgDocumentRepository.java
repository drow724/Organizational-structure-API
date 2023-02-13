package com.org.api.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.org.api.document.OrgDocument;

public interface OrgDocumentRepository extends ReactiveMongoRepository<OrgDocument, Long> {

}
