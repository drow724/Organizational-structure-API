package com.org.controller;

import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.service.ReorganizeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReorganizeController {

	private final ReorganizeService reorganizeService;
	
    @MessageMapping("currentMarketData")
    public Mono<MarketData> currentMarketData(MarketDataRequest marketDataRequest) {
        return marketDataRepository.getOne(marketDataRequest.getStock());
    }
}
