package com.org.api.filter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class ThrottleFilter implements WebFilter {
	
	private final ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
	
	@Scheduled(cron = "0 0 0 * * *")
    public Disposable invalidateOtp(){

       return Mono.fromCallable(() -> {
    	   map.clear();
    	   return Mono.just(Boolean.TRUE).then();
       }).subscribe();

    }
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		if(exchange.getRequest().getMethod().equals(HttpMethod.POST) && exchange.getRequest().getURI().toString().contains("orgMapping")) {
			if(map.get(String.valueOf(exchange.getRequest().getRemoteAddress().getAddress())) != null){
	            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	            return exchange.getResponse().setComplete();
	        } else {
	        	map.put(String.valueOf(exchange.getRequest().getRemoteAddress().getAddress()), String.valueOf(exchange.getRequest().getRemoteAddress().getAddress()));
	        }
		}
		
		return chain.filter(exchange);
	}
}