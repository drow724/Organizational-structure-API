package com.org.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.dto.OrgMappingDTO;
import com.org.entity.OrgDocument;
import com.org.entity.Organization;
import com.org.repository.OrgMongoRepository;
import com.org.repository.OrgPersistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationService {

	private final OrgPersistRepository orgPersistRepository;

	private final OrgMongoRepository orgMongoRepository;

	public void organize(List<OrgMappingDTO> list) {
		orgPersistRepository
				.saveAll(list.parallelStream().map(map -> new Organization(map)).collect(Collectors.toList()))
				.collectList()
				.doOnNext(orgs -> orgMongoRepository
						.saveAll(orgs.parallelStream().map(org -> new OrgDocument(org)).toList()).subscribe())
				.subscribe();
	}

	public void deleteAll() {
		orgPersistRepository.deleteAll().subscribe();
		orgMongoRepository.deleteAll().subscribe();
	}
}
