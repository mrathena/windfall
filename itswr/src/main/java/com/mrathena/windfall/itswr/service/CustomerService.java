package com.mrathena.windfall.itswr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.mrathena.windfall.itswr.bo.Progress;
import com.mrathena.windfall.itswr.entity.Customer;
import com.mrathena.windfall.itswr.mapper.CustomerMapper;
import com.mrathena.windfall.itswr.tool.OkHttpKit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

	private static final String PROGRESS = "PROGRESS";
	private static final String CD = "CD";
	private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0";

	private Lock lock = new ReentrantLock();

	@Autowired
	private Environment env;

	@Autowired
	private CustomerMapper mapper;

	public String crawl(String startNo, Integer count, ServletContext context) {
		// 任务进程校验
		Object flag = context.getAttribute(PROGRESS);
		if (flag != null) {
			return "有一个爬取任务正在进行中, 请在其爬取结束后再开启新的爬取任务";
		}
		Progress progress = new Progress("初始化");
		context.setAttribute(PROGRESS, progress);
		try {
			lock.lock();
			// 参数校验
			if (startNo == null || startNo.isEmpty()) {
				return "起始CD号的格式应该为[CDxxxxxxx]";
			}
			if (count == null) {
				return "CD号个数不能为空";
			}
			if (count < 1) {
				return "CD号个数不能小于1";
			}
			if (10000 < count) {
				return "CD号个数不能大于10000";
			}
			Pattern pattern = Pattern.compile("CD[1-9][0-9]{6}");
			Matcher matcher = pattern.matcher(startNo);
			if (!matcher.matches()) {
				return "起始CD号的格式应该为[CDxxxxxxx]";
			}
			int start = Integer.parseInt(startNo.substring(2));
			int stop = start + count - 1;
			progress.setStartNo(CD + start);
			progress.setStopNo(CD + stop);
			context.setAttribute(PROGRESS, progress);
			// 获取itswr账号密码
			String username = env.getProperty("itswr.username");
			String password = env.getProperty("itswr.password");
			// 登录itswr
			boolean success = authenticate(username, password, context, progress);
			if (!success) {
				return "Itswr网站鉴权失败";
			}
			// 开100个线程遍历获取信息
			ExecutorService executor = Executors.newFixedThreadPool(100);
			for (int i = 0; i <= stop - start; i++) {
				int index = start + i;

				progress.setStatus("执行中");
				context.setAttribute(PROGRESS, progress);
				// 获取信息
				String id = CD + index;
				executor.submit(new Runnable() {
					@Override
					public void run() {

						long start = System.currentTimeMillis();
						log.info("执行:开始:[{}],({},{})", id, progress.getStartNo(), progress.getStopNo());
						Customer oldCustomer = mapper.selectByNo(id);
						if (oldCustomer != null && "SUCCESS".equals(oldCustomer.getStatus())) {
							long end = System.currentTimeMillis();
							log.info("执行:结束:[{}]:{}", id, end - start);
							return;
						}
						// oldCustomer 没有/有但失败
						int counter = 1;
						Customer customer = getCustomer(id);
						while (counter < 10 && "FAILURE".equals(customer.getStatus())) {
							log.info("执行:失败:[{}],第{}次重试", id.toUpperCase(), counter);
							counter++;
							customer = getCustomer(id);
						}
						if ("SUCCESS".equals(customer.getStatus())) {
							log.info("执行:成功:[{}]", id.toUpperCase());
							progress.getSuccess().add(id);
							context.setAttribute(PROGRESS, progress);
							if (oldCustomer == null) {
								mapper.insertSelective(customer);
							} else {
								customer.setId(oldCustomer.getId());
								mapper.updateByPrimaryKeySelective(customer);
							}
						} else {
							log.info("执行:失败:[{}]", id.toUpperCase());
							progress.getFailure().add(id);
							context.setAttribute(PROGRESS, progress);
							if (oldCustomer == null) {
								mapper.insertSelective(customer);
							}
						}
						long end = System.currentTimeMillis();
						log.info("执行:结束:[{}]:{}", id, end - start);
					}
				});
			}
			executor.shutdown();
			boolean loop = true;
			do { //等待所有任务完成
				loop = !executor.awaitTermination(1, TimeUnit.SECONDS); //阻塞，直到线程池里所有任务结束
			} while (loop);
			progress.setStatus("结束");
			context.setAttribute(PROGRESS, progress);
			return "任务执行成功";
		} catch (Exception e) {
			log.error("", e);
			return "任务执行失败";
		} finally {
			lock.unlock();
			// 移除标记
			context.removeAttribute(PROGRESS);
		}
	}

	public Progress status(ServletContext context) {
		Object flag = context.getAttribute(PROGRESS);
		if (flag == null) {
			return new Progress("当前没有正在执行的任务");
		}
		return (Progress) flag;
	}

	public List<Customer> query(String startNo, Integer size, Integer index) {
		if (startNo != null && startNo.isEmpty()) {
			return new ArrayList<>();
		}
		if (size == null || size < 10) {
			size = 10;
		}
		if (index == null || index < 1) {
			index = 1;
		}
		PageHelper.startPage(index, size);
		return mapper.select(startNo);
	}

	private boolean authenticate(String username, String password, ServletContext context, Progress progress) {
		try {
			// 请求参数
			Map<String, Object> parameters = new HashMap<>();

			// 获取登录参数
			log.info("获取登录参数");
			progress.setStatus("初始化:获取登录参数");
			context.setAttribute(PROGRESS, progress);
			String url = "https://itswr.prometric.com/login.aspx";
			String response = OkHttpKit.get(url).userAgent(UA).cookie("AspxAutoDetectCookieSupport=1").execute2();
			Document document = Jsoup.parse(response);

			// 登录(获取SecurityServices)
			log.info("登录");
			progress.setStatus("初始化:登录");
			context.setAttribute(PROGRESS, progress);
			url = "https://itswr.prometric.com/login.aspx";
			parameters.put("__EVENTTARGET", "");
			parameters.put("__EVENTARGUMENT", "");
			parameters.put("__EVENTVALIDATION", document.getElementById("__EVENTVALIDATION").val());
			parameters.put("__VIEWSTATE", document.getElementById("__VIEWSTATE").val());
			parameters.put("__VIEWSTATEENCRYPTED", document.getElementById("__VIEWSTATEENCRYPTED").val());
			parameters.put("hdnInput", document.getElementById("hdnInput").val());
			parameters.put("ctl00$_ContentPlaceHolder$login$UserName", username);
			parameters.put("ctl00$_ContentPlaceHolder$login$Password", password);
			parameters.put("ctl00$_ContentPlaceHolder$login$LoginButton", "Log In");
			response = OkHttpKit.post(url).userAgent(UA).parameters(parameters).cookie("AspxAutoDetectCookieSupport=1")
					.execute2();
			document = Jsoup.parse(response);

			// 选择结点(CN52N)
			log.info("选择结点(CN52N)");
			progress.setStatus("初始化:选择结点(CN52N)");
			context.setAttribute(PROGRESS, progress);
			url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
			parameters.clear();
			parameters.put("__EVENTARGUMENT", "click-0");
			parameters.put("__EVENTTARGET", "ctl00$_HeaderPlaceHolder$siteDropdown");
			parameters.put("__EVENTVALIDATION", document.getElementById("__EVENTVALIDATION").val());
			parameters.put("__VIEWSTATE", document.getElementById("__VIEWSTATE").val());
			parameters.put("__VIEWSTATEENCRYPTED", document.getElementById("__VIEWSTATEENCRYPTED").val());
			parameters.put("ctl00$hdnInput", document.getElementById("hdnInput").val());
			response = OkHttpKit.post(url).userAgent(UA).cookie("AspxAutoDetectCookieSupport=1").parameters(parameters)
					.execute2();

			return true;
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
	}

	private Customer getCustomer(String id) {
		Customer customer = new Customer();
		customer.setNo(id);
		customer.setStatus("FAILURE");
		try {
			// search页面
			String url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
			String responseStr = OkHttpKit.get(url).userAgent(UA).cookie("AspxAutoDetectCookieSupport=1").execute2();
			Document document = Jsoup.parse(responseStr);

			// 获取信息(prometricTestingId对应的key(会变))
			url = "https://itswr.prometric.com/SiteScheduler/Services/SearchService.svc/TestingID/";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("prometricTestingId", id);
			responseStr = OkHttpKit.post(url).userAgent(UA).cookie("AspxAutoDetectCookieSupport=1")
					.json(JSON.toJSONString(parameters)).execute2();
			String arguement = JSON.parseObject(responseStr).getJSONArray("r").getJSONObject(0).getString("i");
			// 获取信息
			url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
			parameters.clear();
			parameters.put("__EVENTARGUMENT", arguement);
			parameters.put("__EVENTTARGET", "ctl00$_ContentPlaceHolder$selectClick");
			parameters.put("__EVENTVALIDATION", document.getElementById("__EVENTVALIDATION").val());
			parameters.put("__VIEWSTATE", document.getElementById("__VIEWSTATE").val());
			parameters.put("__VIEWSTATEENCRYPTED", document.getElementById("__VIEWSTATEENCRYPTED").val());
			parameters.put("ctl00$_ContentPlaceHolder$areaCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$clientCandidateId$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$confirmationNumber$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$countryCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$firstName$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$lastName$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$phoneNumber$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$postalCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$prometricTestingId$textBox", "cd1000001");
			parameters.put("ctl00$_ContentPlaceHolder$searchByList", "TestingID");
			parameters.put("ctl00$hdnInput", document.getElementById("hdnInput").val());
			parameters.put("sortByCriteria", "name");
			responseStr = OkHttpKit.post(url).userAgent(UA).cookie("AspxAutoDetectCookieSupport=1")
					.parameters(parameters).execute2();

			String[] lines = responseStr.split(System.lineSeparator());
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (line.contains("var a")) {
					line = line.substring(line.indexOf("{"));
					line = line.substring(0, line.length() - 1);
					JSONObject object = JSON.parseObject(line);
					JSONObject data = object.getJSONObject("data");
					log.info("[{}]:{}", id.toUpperCase(), line);
					if (data == null) {
						return customer;
					}
					JSONObject info = data.getJSONObject("i");
					if (info == null) {
						return customer;
					}
					customer.setStatus("SUCCESS");
					String email = info.getString("e");
					String firstName = info.getString("f");
					String lastName = info.getString("l");
					String primaryNumber = info.getJSONObject("p").getString("n");
					String secondlyNumber = info.getJSONObject("s").getString("n");
					String name = firstName + " " + lastName;
					customer.setName(name);
					customer.setEmail(email);
					customer.setNumber(primaryNumber);
					customer.setNumber2(secondlyNumber);
					return customer;
				}
			}
			return customer;
		} catch (Exception e) {
			log.error("", e);
			return customer;
		}
	}

}