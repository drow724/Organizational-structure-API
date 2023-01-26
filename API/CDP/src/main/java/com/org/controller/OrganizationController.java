package com.org.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.dto.OrgMappingDTO;
import com.org.entity.Organization;
import com.org.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OrganizationController {

	private final OrganizationService organizationService;

	@MessageMapping("orgMapping")
	public Mono<Boolean> currentMarketData(List<OrgMappingDTO> list) {
		return organizationService.organize(list).then(Mono.just(Boolean.TRUE));
	}

	@GetMapping("/api/organization")
	public Mono<Page<Organization>> retreiveOrg(
			@PageableDefault(page = 0, size = 10, sort = "hqCd", direction = Sort.Direction.DESC) Pageable pageable) {
		return organizationService.retriveOrg(pageable);
	}
}
