package com.org.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.org.entity.Organization;

import reactor.core.publisher.Flux;

public interface OrganizationRepository extends R2dbcRepository<Organization, Long> {
	Flux<Organization> findBy(Pageable pageable);
}
