package com.mrathena.windfall.itswr.entity;

/** itswr_customer */
public class Customer {

	/** itswr_customer.id */
	private Long id;
	/** itswr_customer.no */
	private String no;
	/** itswr_customer.name */
	private String name;
	/** itswr_customer.email */
	private String email;
	/** itswr_customer.number */
	private String number;
	/** itswr_customer.number2 */
	private String number2;
	/** itswr_customer.status */
	private String status;

	/** itswr_customer.id */
	public Long getId() {
		return id;
	}
	/** itswr_customer.id */
	public Customer withId(Long id) {
		this.setId(id);
		return this;
	}
	/** itswr_customer.id */
	public void setId(Long id) {
		this.id = id;
	}
	/** itswr_customer.no */
	public String getNo() {
		return no;
	}
	/** itswr_customer.no */
	public Customer withNo(String no) {
		this.setNo(no);
		return this;
	}
	/** itswr_customer.no */
	public void setNo(String no) {
		this.no = no == null ? null : no.trim();
	}
	/** itswr_customer.name */
	public String getName() {
		return name;
	}
	/** itswr_customer.name */
	public Customer withName(String name) {
		this.setName(name);
		return this;
	}
	/** itswr_customer.name */
	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}
	/** itswr_customer.email */
	public String getEmail() {
		return email;
	}
	/** itswr_customer.email */
	public Customer withEmail(String email) {
		this.setEmail(email);
		return this;
	}
	/** itswr_customer.email */
	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}
	/** itswr_customer.number */
	public String getNumber() {
		return number;
	}
	/** itswr_customer.number */
	public Customer withNumber(String number) {
		this.setNumber(number);
		return this;
	}
	/** itswr_customer.number */
	public void setNumber(String number) {
		this.number = number == null ? null : number.trim();
	}
	/** itswr_customer.number2 */
	public String getNumber2() {
		return number2;
	}
	/** itswr_customer.number2 */
	public Customer withNumber2(String number2) {
		this.setNumber2(number2);
		return this;
	}
	/** itswr_customer.number2 */
	public void setNumber2(String number2) {
		this.number2 = number2 == null ? null : number2.trim();
	}
	/** itswr_customer.status */
	public String getStatus() {
		return status;
	}
	/** itswr_customer.status */
	public Customer withStatus(String status) {
		this.setStatus(status);
		return this;
	}
	/** itswr_customer.status */
	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

}