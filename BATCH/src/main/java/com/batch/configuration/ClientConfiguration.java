package com.batch.configuration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;

import reactor.util.retry.Retry;

@Configuration
public class ClientConfiguration {

    @Bean
    public RSocketRequester getRSocketRequester(RSocketStrategies rSocketStrategies){
        RSocketRequester.Builder builder = RSocketRequester.builder();

        return builder
          .rsocketConnector(
             rSocketConnector ->
               rSocketConnector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
          )
          .rsocketStrategies(rSocketStrategies)
          .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
          .tcp("localhost", 8083);
    }
}
