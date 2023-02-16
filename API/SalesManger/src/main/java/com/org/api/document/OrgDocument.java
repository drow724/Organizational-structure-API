package com.org.api.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.org.api.annotation.Excel;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "orgs")
@Getter
@NoArgsConstructor
public class OrgDocument {

	@Id
	private Long id;
	
	@Excel(title = "본부 조직 코드")
	private String hqCd;

	@Excel(title = "본부 이름")
	private String hqNm;

	@Excel(title = "지점 조직 코드")
	private String agncCd;

	@Excel(title = "지점 이름")
	private String agncNm;

	@Excel(title = "GA 조직 코드")
	private String gaCd;

	@Excel(title = "GA 이름")
	private String gaNm;

	@Excel(title = "지사 조직 코드")
	private String brnCd;

	@Excel(title = "지사 이름")
	private String brnNm;

}