package com.batch.configuration.batch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.batch.dto.OrgMappingDTO;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StepConfiguration {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	
	private final StepBuilderFactory stepBuilderFactory;

	private final AmazonS3 amazonS3;
	
	@Bean
	public ListItemReader<Map<String, Object>> reader() {
		S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, "organization.xlsx"));
        S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
        byte[] bytes = null;
		try {
			bytes = IOUtils.toByteArray(objectInputStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        InputStream bis = new ByteArrayInputStream(bytes);
        try {
        	Workbook workBook = new HSSFWorkbook(bis);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return new ListItemReader<Map<String, Object>>(new ArrayList<>());
	}
	
	@Bean
	public ItemWriter<OrgMappingDTO> writer() {
		return new ItemWriter<OrgMappingDTO>() {
			@Override
			public void write(List<? extends OrgMappingDTO> items) throws Exception {
				
			}
		};
	}
	
	@Bean
	public Step step() {
        return stepBuilderFactory.get("step")
                .<Map<String, Object>,OrgMappingDTO>chunk(100)
                .reader(reader())
                .writer(writer())
                .build();
    }
}
