package com.org.api.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressDTO {

	public ProgressDTO(Map<String, Object> data) {
		this.all = (Long) data.get("all");
		this.data = (Long) data.get("data");
	}

	private Long all;

	private Long data;
}
