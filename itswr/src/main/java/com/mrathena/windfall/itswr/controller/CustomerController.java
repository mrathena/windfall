package com.mrathena.windfall.itswr.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrathena.windfall.itswr.bo.DataTables;
import com.mrathena.windfall.itswr.bo.Progress;
import com.mrathena.windfall.itswr.entity.Customer;
import com.mrathena.windfall.itswr.service.CustomerService2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("customer")
public class CustomerController {

	@Autowired
	private CustomerService2 service;

	@PostMapping("list")
	public Object index(DataTables dt, String startNo) {
		log.info("Controller::Request::Parameters::startNo:{},datatables:{}", startNo, dt);
		DataTables.Result<Customer> page = service.query(dt, startNo);
		log.info("Controller::Response::{}", page);
		return page;
	}

	@PostMapping("crawl")
	public Object crawl(String startNo, Integer count, HttpServletRequest request) {
		log.info("Controller::Request::Parameters::startNo:{},count:{}", startNo, count);
		ServletContext context = request.getSession().getServletContext();
		String resposne = service.crawl(startNo, count, context);
		log.info("Controller::Response::{}", resposne);
		return resposne;
	}

	@PostMapping("status")
	public Object status(HttpServletRequest request) {
		// log.info("Controller::Request::Parameters::");
		ServletContext context = request.getSession().getServletContext();
		Progress progress = service.status(context);
		// log.info("Controller::Response::{}", progress);
		return progress;
	}

}
