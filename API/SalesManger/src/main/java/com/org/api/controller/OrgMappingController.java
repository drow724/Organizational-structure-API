package com.org.api.controller;

import java.util.Map;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.org.api.dto.FileDTO;
import com.org.api.service.OrgMappingService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequiredArgsConstructor
public class OrgMappingController {

	private final Sinks.Many<Map<String, Object>> sink = Sinks.many().multicast().onBackpressureBuffer();

	private final OrgMappingService orgMappingService;

	@PostMapping("orgMapping")
	public ResponseEntity<String> OrgMapping(@RequestBody FileDTO document) {
		orgMappingService.mapping(document.getFile());
		return ResponseEntity.ok("Commit");
	}

	@GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Map<String, Object>> progress() {
		return sink.asFlux();
	}
}
