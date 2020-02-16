package com.frauddetection.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "expenses")
@Getter
@Setter
@Builder
public class ExpenseEntity {
	public ExpenseEntity() {
	}

	public ExpenseEntity(Long id, String filename, boolean status, String reason, String employeenumber, String totalAmount) {
		this.id = id;
		this.filename = filename;
		this.status = status;
		this.reason = reason;
		this.employeenumber = employeenumber;
		this.totalAmount = totalAmount;
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="idfrom") 
	private Long id;

	private String filename;

	private boolean status;
	
	private String reason;

	private String employeenumber;
	
	private String totalAmount;
}
