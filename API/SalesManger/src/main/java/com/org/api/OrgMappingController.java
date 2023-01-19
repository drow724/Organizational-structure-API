package com.org.api;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequiredArgsConstructor
public class OrgMappingController {

	private final Sinks.Many<Map<String, Object>> sink = Sinks.many().multicast().onBackpressureBuffer();

	private final OrgMappingService orgMappingService;

	@PostMapping("orgMapping")
	public void OrgMapping(@RequestBody FileDTO document) {
		orgMappingService.mapping(document.getFile());
	}

	@GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Map<String, Object>> progress() {
		return sink.asFlux();
	}
}
