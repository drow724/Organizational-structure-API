package com.org.api.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "orgs")
@Getter
@NoArgsConstructor
public class OrgDocument {

	@Id
	private Long id;
	
	private String hqCd;

	private String hqNm;

	private String agncCd;

	private String agncNm;

	private String gaCd;

	private String gaNm;

	private String brnCd;

	private String brnNm;

}