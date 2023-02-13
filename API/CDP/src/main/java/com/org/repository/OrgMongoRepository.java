package com.org.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.org.entity.OrgDocument;

public interface OrgMongoRepository extends ReactiveMongoRepository<OrgDocument, Long> {

}
