package com.org.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.org.entity.Organization;

public interface OrgPersistRepository extends R2dbcRepository<Organization, Long> {
}
