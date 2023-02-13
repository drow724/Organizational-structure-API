package com.org.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.org.dto.OrgMappingDTO;
import com.org.entity.OrgDocument;
import com.org.entity.Organization;
import com.org.repository.OrgMongoRepository;
import com.org.repository.OrgPersistRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrganizationService {

	private final OrgPersistRepository orgPersistRepository;

	private final OrgMongoRepository orgMongoRepository;

	public Flux<OrgDocument> organize(List<OrgMappingDTO> list) {
		return orgPersistRepository
				.saveAll(list.parallelStream().map(map -> new Organization(map)).collect(Collectors.toList()))
				.map(org -> new OrgDocument(org)).doOnNext(org -> orgMongoRepository.save(org));
	}

	public Flux<OrgDocument> retriveOrg(Pageable pageable) {
		return orgMongoRepository.findAll(pageable.getSort()).skip(pageable.getOffset()).take(pageable.getPageSize());
	}

	public Mono<Void> deleteAll() {
		return orgPersistRepository.deleteAll();
	}
}
