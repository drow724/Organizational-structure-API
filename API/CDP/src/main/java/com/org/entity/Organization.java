package com.org.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.org.dto.OrgMappingDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("organization")
@AllArgsConstructor
public class Organization {

	@Id
	private Long id;
	
	@Column("HQ_CD")
	private String hqCd;

	@Column("HQ_NM")
	private String hqNm;

	@Column("AGNC_CD")
	private String agncCd;

	@Column("AGNC_NM")
	private String agncNm;

	@Column("GA_CD")
	private String gaCd;

	@Column("GA_NM")
	private String gaNm;

	@Column("BRN_CD")
	private String brnCd;

	@Column("BRN_NM")
	private String brnNm;
	
	public Organization() {
		
	}
	
	public Organization(OrgMappingDTO dto) {
		this.hqCd = dto.getHqCd();
		this.hqNm = dto.getHqNm();
		this.agncCd = dto.getAgncCd();
		this.agncNm = dto.getAgncNm();
		this.gaCd = dto.getGaCd();
		this.gaNm = dto.getGaNm();
		this.brnCd = dto.getBrnCd();
		this.brnNm = dto.getBrnNm();
	}
}
