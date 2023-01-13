package com.batch.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Service;
import org.springframework.util.MethodInvoker;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.batch.annotation.Excel;
import com.batch.dto.OrgMappingDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {

	private final ExcelService excelService;

	private final StepBuilderFactory stepBuilderFactory;

	private final JobBuilderFactory jobBuilderFactory;

	private final JobLauncher launcher;

	public BatchStatus batch(MultipartFile file) {

		List<Map<String, Object>> list = excelService.read(file);

		Step step = stepBuilderFactory.get("step").<Map<String, Object>, OrgMappingDTO>chunk(100)
				.reader(new ListItemReader<Map<String, Object>>(list))
				.processor(new ItemProcessor<Map<String, Object>, OrgMappingDTO>() {
					
					@Override
					public OrgMappingDTO process(Map<String, Object> item) throws Exception {
						OrgMappingDTO dto = new OrgMappingDTO();
						for (String key : item.keySet()) {
							MethodInvoker invoker = new MethodInvoker();
							for (Field field : dto.getClass().getDeclaredFields()) {
								if (!field.isSynthetic() && !field.getName().startsWith("$")
										&& key.equals(field.getDeclaredAnnotation(Excel.class).title())) {
									invoker.setTargetMethod("set" + String.valueOf(field.getName().charAt(0)).toUpperCase()
											+ field.getName().substring(1, field.getName().length()));
									invoker.setTargetObject(dto);
									invoker.setArguments(item.get(key));
									invoker.prepare();
									invoker.invoke();
								}
							}
						}
						return dto;
					}
				}).writer(new ItemWriter<OrgMappingDTO>() {
					@Override
					public void write(List<? extends OrgMappingDTO> items) throws Exception {
					}
				}).build();

		Job job = jobBuilderFactory.get("job").start(step).build();

		JobExecution execution = null;

		try {
			execution = launcher.run(job, new JobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return execution.getStatus();
	}
}
