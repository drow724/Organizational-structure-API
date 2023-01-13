package com.org.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReorganizeService {

	@Async
	public void reorganize() throws URISyntaxException {
		WebSocketClient client = new ReactorNettyWebSocketClient();

		URI url = new URI("ws://localhost:8080/path");
		client.execute(url, session -> session.receive().doOnNext(System.out::println).then());

	}

}
