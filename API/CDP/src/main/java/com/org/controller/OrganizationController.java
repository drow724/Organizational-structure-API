package com.org.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.dto.OrgMappingDTO;
import com.org.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OrganizationController {

	private final OrganizationService organizationService;

	@MessageMapping("before")
	public Mono<Boolean> deleteAll() {
		return organizationService.deleteAll().then(Mono.just(Boolean.TRUE));
	}
	
	@MessageMapping("orgMapping")
	public Mono<Boolean> addList(List<OrgMappingDTO> list) {
		organizationService.organize(list);
		return Mono.just(Boolean.TRUE);
	}

}
