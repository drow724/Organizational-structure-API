package com.batch.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;

	@Bean
	public Job job(Step step) {
		return jobBuilderFactory.get("job").start(step).build();
	}
}
