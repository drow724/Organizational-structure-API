package com.org.entity;

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
	
	public OrgDocument(Organization org) {
		this.id = org.getId();
		this.hqCd = org.getHqCd();
		this.hqNm = org.getHqNm();
		this.agncCd = org.getAgncCd();
		this.agncNm = org.getAgncNm();
		this.gaCd = org.getGaCd();
		this.gaNm = org.getGaNm();
		this.brnCd = org.getBrnCd();
		this.brnNm = org.getBrnNm();
	}
}
