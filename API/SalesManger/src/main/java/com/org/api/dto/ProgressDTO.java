package com.org.api.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressDTO {

	public ProgressDTO(Map<String, Object> data) {
		this.all = (Integer) data.get("all");
		this.data = (Integer) data.get("data");
	}

	private Integer all;

	private Integer data;
}
