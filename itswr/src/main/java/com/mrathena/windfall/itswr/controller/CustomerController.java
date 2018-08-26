package com.mrathena.windfall.itswr.controller;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrathena.windfall.itswr.bo.Progress;
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
	public Object index(String startNo, Integer size, Integer index) {
		log.info("Controller::Request::Parameters::startNo:{},size:{},index:{}", startNo, size, index);
		List<Customer> customerList = service.query(startNo, size, index);
		log.info("Controller::Response::{}", customerList);
		return customerList;
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
		log.info("Controller::Request::Parameters::");
		ServletContext context = request.getSession().getServletContext();
		Progress progress = service.status(context);
		log.info("Controller::Response::{}", progress);
		return progress;
	}

}
