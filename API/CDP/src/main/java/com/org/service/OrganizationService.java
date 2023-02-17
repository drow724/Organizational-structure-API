package com.org.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

import com.org.dto.OrgMappingDTO;
import com.org.entity.OrgDocument;
import com.org.entity.Organization;
import com.org.repository.OrgMongoRepository;
import com.org.repository.OrgPersistRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrganizationService {

	private final OrgPersistRepository orgPersistRepository;

	private final OrgMongoRepository orgMongoRepository;

	private final RSocketRequester rSocketRequester;

	public void organize(List<OrgMappingDTO> list) {
		orgPersistRepository
				.saveAll(list.parallelStream().map(map -> new Organization(map)).collect(Collectors.toList()))
				.collectList()
				.doOnNext(orgs -> orgMongoRepository
						.saveAll(orgs.parallelStream().map(org -> new OrgDocument(org)).toList()).subscribe())
				.doOnNext(orgs -> {
					Map<String, Object> map = new HashMap<>();
					map.put("data", orgs.size());
					rSocketRequester.route("progress").data(map).send().subscribe();
					})
				.subscribe();
	}

	public Mono<Boolean> deleteAll() {
		return orgMongoRepository.count().flatMap(count -> {
			Map<String, Object> map = new HashMap<>();
			map.put("all", count);
			map.put("data", 0);
			rSocketRequester.route("progress").data(map).send().subscribe();
			orgPersistRepository.deleteAll().subscribe();
			orgMongoRepository.deleteAll().subscribe();

			return Mono.just(Boolean.TRUE);
		});
		
	}
	
	public Mono<Void> done() {
		Map<String, Object> map = new HashMap<>();
		map.put("isStart", Boolean.FALSE);
		return Mono.just(rSocketRequester.route("after").data(map).send().subscribe()).then();
	}
}
