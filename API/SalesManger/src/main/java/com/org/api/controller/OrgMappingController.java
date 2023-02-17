package com.org.api.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.org.api.document.OrgDocument;
import com.org.api.service.OrgMappingService;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@RestController
@CrossOrigin
public class OrgMappingController {

	private Many<Map<String, Object>> sinks = Sinks.many().multicast().onBackpressureBuffer();

	private final OrgMappingService orgMappingService;

	private final Map<String, Object> map = new ConcurrentHashMap<>();
	
	public OrgMappingController(OrgMappingService orgMappingService) {
		map.put("isStart", Boolean.FALSE);
		this.orgMappingService = orgMappingService;
	}
	
	@MessageMapping("progress")
	public Mono<Void> channel(final Map<String, Object> data) {
		if (data.get("isStart") != null) {
			return Mono.just(map.put("isStart", (Boolean) data.get("isStart"))).then();
		}
		if (data.get("all") != null) {
			map.put("all", data.get("all"));
			map.put("data", null);
		}
		if (data.get("data") != null) {
			if (map.get("data") != null) {
				map.put("data", (Integer) map.get("data") + (Integer) data.get("data"));
			} else {
				map.put("data", data.get("data"));
			}

		}
		return Mono.just(sinks.tryEmitNext(map)).then();
	};

	@PostMapping("orgMapping")
	public Mono<ResponseEntity<String>> OrgMapping(@RequestPart("file") Mono<FilePart> file) {
		return file.filter(f -> map.get("isStart") != null && !(Boolean) map.get("isStart"))
				.doOnNext(f -> orgMappingService.mapping(f.filename(), f.content()).subscribe())
				.doOnSuccess(t -> map.put("isStart", Boolean.TRUE)).then(Mono.just(ResponseEntity.ok("Commit")))
				.switchIfEmpty(Mono.just(ResponseEntity.ok("isWorking")));
	}

	@GetMapping("status")
	public Mono<Boolean> getStatus() {
		return Mono.just((Boolean) map.get("isStart"));
	}

	@GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<Map<String, Object>>> progress() {
		return sinks.asFlux().filter(e -> e != null && e.get("data") != null)
				.flatMap(e -> Mono.just(ServerSentEvent.builder(e).build())).switchIfEmpty(Flux.empty());
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
