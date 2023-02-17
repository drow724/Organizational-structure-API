package com.org.api.filter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class ThrottleFilter implements WebFilter {
	
	private final ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		
		if(exchange.getRequest().getMethod().equals(HttpMethod.POST) && exchange.getRequest().getURI().toString().contains("orgMapping")) {
			if(map.get(String.valueOf(exchange.getRequest().getRemoteAddress())) != null){
	            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	            return exchange.getResponse().setComplete();
	        } else {
	        	map.put(String.valueOf(exchange.getRequest().getRemoteAddress()), String.valueOf(exchange.getRequest().getRemoteAddress()));
	        }
		}
		
		return chain.filter(exchange);
	}
}