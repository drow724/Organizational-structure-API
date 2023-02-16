package com.org.api.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.org.api.document.OrgDocument;
import com.org.api.service.OrgMappingService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class OrgMappingController {

	private final Sinks.Many<Map<String, Object>> sink = Sinks.many().multicast().onBackpressureBuffer();

	private final OrgMappingService orgMappingService;

	@MessageMapping("progress")
	public void channel(final Map<String, Object> data) {
		sink.tryEmitNext(data);
	};

	@PostMapping("orgMapping")
	public Mono<ResponseEntity<String>> OrgMapping(@RequestPart("file") Mono<FilePart> file) {
		return file.doOnNext(f -> orgMappingService.mapping(f.filename(), f.content()).subscribe())
				.then(Mono.just(ResponseEntity.ok("Commit")));
	}

	@GetMapping("status")
	public Mono<String> getStatus() {
		return orgMappingService.getStatus();
	}

	@GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Map<String, Object>> progress() {
		return sink.asFlux();
	}

	@GetMapping(value = "orgMapping/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Mono<byte[]>> retrieveOrgExcel() throws IOException {

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "조직구조")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.body(orgMappingService.excel());
	}

	@GetMapping("orgMapping")
	public Mono<Page<OrgDocument>> retrieveOrg(
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		return orgMappingService.findAll(pageable);
	}
}
