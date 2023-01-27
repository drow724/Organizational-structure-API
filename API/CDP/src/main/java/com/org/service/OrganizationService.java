package com.org.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.org.dto.OrgMappingDTO;
import com.org.entity.Organization;
import com.org.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrganizationService {

	private final OrganizationRepository organizationRepository;

	public Flux<Organization> organize(List<OrgMappingDTO> list) {
		return organizationRepository
				.saveAll(list.parallelStream().map(map -> new Organization(map)).collect(Collectors.toList()));
	}

	public Mono<Page<Organization>> retriveOrg(Pageable pageable) {
		return organizationRepository.findBy(pageable).collectList().zipWith(organizationRepository.count())
				.map(data -> new PageImpl<Organization>(data.getT1(), pageable, data.getT2()));
	}

	public Mono<Void> deleteAll() {
		return organizationRepository.deleteAll();
	}
}
