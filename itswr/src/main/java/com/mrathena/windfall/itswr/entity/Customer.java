package com.mrathena.windfall.itswr.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Customer {

	private Long id;
	private String no;
	private String name;
	private String email;
	private String number;
	private String number2;
	private String status;
	private String reason;

}