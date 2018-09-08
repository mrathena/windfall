package com.mrathena.windfall.itswr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrathena.windfall.itswr.bo.DataTables;
import com.mrathena.windfall.itswr.bo.Status;
import com.mrathena.windfall.itswr.entity.Customer;
import com.mrathena.windfall.itswr.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("customer")
public class CustomerController {

	@Autowired
	private CustomerService service;

	@PostMapping("list")
	public Object index(DataTables dt, String startNo) {
		log.info("Controller::Request::Parameters::startNo:{},datatables:{}", startNo, dt);
		DataTables.Result<Customer> page = service.query(dt, startNo);
		log.info("Controller::Response::{}", page);
		return page;
	}

	@PostMapping("crawl")
	public Object crawl(String perfix, String start, String end) throws Exception {
		log.info("Controller::Request::Parameters::perfix:{},start:{},end:{}", perfix, start, end);
		String resposne = service.crawl(perfix, start, end);
		log.info("Controller::Response::{}", resposne);
		return resposne;
	}

	@PostMapping("status")
	public Object status() {
		// log.info("Controller::Request::Parameters::");
		Status status = service.status();
		// log.info("Controller::Response::{}", progress);
		return status;
	}

}
