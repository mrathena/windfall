package com.mrathena.windfall.itswr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mrathena.windfall.itswr.bo.DataTables;
import com.mrathena.windfall.itswr.bo.Status;
import com.mrathena.windfall.itswr.bo.Status.CrawlStatus;
import com.mrathena.windfall.itswr.common.constant.BusinessConstant;
import com.mrathena.windfall.itswr.common.constant.Constant;
import com.mrathena.windfall.itswr.common.enums.CrawlResult;
import com.mrathena.windfall.itswr.common.enums.SysemStatus;
import com.mrathena.windfall.itswr.entity.Customer;
import com.mrathena.windfall.itswr.mapper.CustomerMapper;
import com.mrathena.windfall.itswr.tool.Http;
import com.mrathena.windfall.itswr.tool.Kit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

	private static final String SUCCESS = CrawlResult.SUCCESS.getCode();
	private static final String FAILURE = CrawlResult.FAILURE.getCode();

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private CustomerMapper mapper;

	public DataTables.Result<Customer> query(DataTables dt, String startNo) {
		PageHelper.startPage(dt.getIndex(), dt.getSize());
		List<Customer> customerList = mapper.selectByStartNo(startNo);
		PageInfo<Customer> page = new PageInfo<>(customerList);
		return new DataTables.Result<>(dt.getDraw(), page.getList(), page.getTotal(), page.getTotal());
	}

	public String crawl(String perfix, long start, long end) throws Exception {
		// 任务进程校验
		Cache cache = cacheManager.getCache(BusinessConstant.CACHE);
		if (cache != null) {
			Status status = cache.get(BusinessConstant.STATUS, Status.class);
			if (status != null) {
				if (SysemStatus.INIT.getCode().equals(status.getStatus())) {
					status.setStatus(SysemStatus.IDLE.getCode());
					return "系统正在执行初始化任务中, 请稍后再试";
				}
				if (SysemStatus.LIVE.getCode().equals(status.getStatus())) {
					status.setStatus(SysemStatus.IDLE.getCode());
					return "系统正在执行保活任务中, 请稍后再试";
				}
				if (SysemStatus.CRAWL.getCode().equals(status.getStatus())) {
					status.setStatus(SysemStatus.IDLE.getCode());
					return "系统正在执行爬取任务中, 请稍后再试";
				}
			}

			if (SysemStatus.IDLE.getCode().equals(status.getStatus())) {

				int total = (int) (end - start + 1);

				// 当前空闲,可执行保活任务
				CrawlStatus crawlStatus = new CrawlStatus(total);
				status.crawl(crawlStatus).setStatus(SysemStatus.CRAWL.getCode());

				// 校验参数
				if (total > BusinessConstant.MAX_CRAWL_COUNT) {
					status.setStatus(SysemStatus.IDLE.getCode());
					return "单次最大爬取数量不能超过10000条";
				}

				String startNo = perfix + start;
				String endNo = perfix + end;

				// 判断数据是否已执行过且成功
				List<Customer> tempCustomerList = mapper.selectByBeginNoAndEndNo(startNo, endNo);
				Map<String, Customer> tempCustomerMap = tempCustomerList.stream().collect(Collectors.toMap(Customer::getNo, customer -> customer));
				long successCount = tempCustomerList.stream().filter(item -> SUCCESS.equals(item.getStatus())).count();
				if (total == tempCustomerList.size() && total == successCount) {
					status.setStatus(SysemStatus.IDLE.getCode());
					return "任务执行结束";
				}

				// 生成no集合
				List<String> noList = new ArrayList<>(total);
				for (int i = 0; i < total; i++) {
					noList.add(perfix + (start + i));
				}

				// 请求Headers
				Map<String, String> headers = new HashMap<>();
				headers.put(BusinessConstant.USER_AGENT, BusinessConstant.USER_AGENT_VALUE);
				headers.put(BusinessConstant.COOKIE, BusinessConstant.COOKIE_VALUE);

				// Http对象集合
				@SuppressWarnings("unchecked")
				List<Http> https = cache.get(BusinessConstant.HTTPS, List.class);

				// 计数器
				AtomicInteger successCounter = new AtomicInteger(Constant.INT_0);
				AtomicInteger failureCounter = new AtomicInteger(Constant.INT_0);

				// 开100个线程遍历获取信息
				ExecutorService executor = Executors.newFixedThreadPool(BusinessConstant.THREAD_COUNT);
				noList.forEach(cdNo -> {
					Customer tempCustomer = tempCustomerMap.get(cdNo);
					if (tempCustomer == null || FAILURE.equals(tempCustomer.getStatus())) {
						// 不存在或失败
						executor.submit(new Runnable() {
							@Override
							public void run() {

								String threadName = Thread.currentThread().getName();
								int index = Integer.parseInt(threadName.substring(threadName.lastIndexOf("-") + 1)) - 1;
								Http http = https.get(index);

								long start = System.currentTimeMillis();
								log.info("--------------");
								log.info("[{}]开始", cdNo);
								log.info("--------------");
								// 重试计数器
								int counter = 1;
								Customer customer = getCustomer(http, headers, cdNo);
								while (counter < BusinessConstant.TRY_TIMES && FAILURE.equals(customer.getStatus())) {
									log.info("--------------");
									log.info("[{}]第{}次重试", cdNo, counter);
									counter++;
									customer = getCustomer(http, headers, cdNo);
								}
								boolean success;
								if (SUCCESS.equals(customer.getStatus())) {
									// 执行成功
									if (tempCustomer == null) {
										// 不存在
										mapper.insertSelective(customer);
									} else {
										// 已存在但失败
										customer.setId(tempCustomer.getId());
										mapper.updateByPrimaryKeySelective(customer);
									}
									crawlStatus.setSuccess(successCounter.incrementAndGet());
									success = true;
								} else {
									// 执行失败
									if (tempCustomer == null) {
										// 不存在
										mapper.insertSelective(customer);
									} else {
										// 已存在且失败
										customer.setId(tempCustomer.getId());
										mapper.updateByPrimaryKeySelective(customer);
									}
									crawlStatus.setFailure(failureCounter.incrementAndGet());
									success = false;
								}
								long end = System.currentTimeMillis();
								log.info("--------------");
								log.info("[{}]结束:{}:{}ms", cdNo, success ? "成功" : "失败", end - start);
								log.info("--------------");
							}
						});
					}
				});
				executor.shutdown();

				boolean loop = true;
				do { // 等待所有任务完成
					loop = !executor.awaitTermination(1, TimeUnit.SECONDS); // 阻塞，直到线程池里所有任务结束
				} while (loop);

				status.setStatus(SysemStatus.IDLE.getCode());

				long success = crawlStatus.getSuccess();
				long failure = crawlStatus.getFailure();
				log.info("爬虫任务: 总数:{}, 成功:{}, 失败:{}", total, success, failure);
				return String.format("爬虫任务: 总数:%d, 成功:%d, 失败:%d", total, success, failure);
			}
		}
		return "喊人吧,这句话出现说明出了系统无法处理的问题";

	}

	public Status status() {
		Cache cache = cacheManager.getCache(BusinessConstant.CACHE);
		return cache.get(BusinessConstant.STATUS, Status.class);
	}

	private Customer getCustomer(Http http, Map<String, String> headers, String no) {
		Customer customer = new Customer();
		customer.setNo(no);
		customer.setStatus("FAILURE");
		try {

			// search页面
			log.info("[{}]跳转到Search页面", no);
			String url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
			log.info("[{}]地址:{}", no, url);
			String response = http.get(url, headers, null);
			Document document = Jsoup.parse(response);
			if (!documentIsLegal(no, document)) {
				customer.setReason("页面Document中参数不全");
				return customer;
			}

			log.info("-");
			// 选择结点(CN52N)
			log.info("[{}]选择节点CN52", no);
			url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
			log.info("[{}]地址:{}", no, url);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("__EVENTARGUMENT", "click-0");
			parameters.put("__EVENTTARGET", "ctl00$_HeaderPlaceHolder$siteDropdown");
			parameters.put("__EVENTVALIDATION", document.getElementById("__EVENTVALIDATION").val());
			parameters.put("__VIEWSTATE", document.getElementById("__VIEWSTATE").val());
			parameters.put("__VIEWSTATEENCRYPTED", document.getElementById("__VIEWSTATEENCRYPTED").val());
			parameters.put("ctl00$hdnInput", document.getElementById("hdnInput").val());
			parameters.put("ctl00$_ContentPlaceHolder$areaCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$clientCandidateId$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$confirmationNumber$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$countryCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$firstName$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$lastName$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$phoneNumber$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$postalCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$prometricTestingId$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$searchByList", "TestingID");
			parameters.put("sortByCriteria", "name");
			log.info("[{}]请求:{}", no, parameters);
			response = http.post(url, headers, parameters);
			document = Jsoup.parse(response);
			if (!documentIsLegal(no, document)) {
				customer.setReason("页面Document中参数不全");
				return customer;
			}

			log.info("-");
			// 获取信息(prometricTestingId对应的key(会变))
			log.info("[{}]获取用户ID", no);
			url = "https://itswr.prometric.com/SiteScheduler/Services/SearchService.svc/TestingID/";
			log.info("[{}]地址:{}", no, url);
			parameters.clear();
			parameters.put("prometricTestingId", no);
			log.info("[{}]请求:{}", no, parameters);
			response = http.post(url, headers, JSON.toJSONString(parameters));
			log.info("[{}]响应:{}", no, response);
			JSONArray jsonArray = JSON.parseObject(response).getJSONArray("r");
			if (jsonArray == null || jsonArray.isEmpty()) {
				log.info("[{}]失败原因:Candidate not found, 这个CD号没有被注册使用", no);
				customer.setReason("Candidate not found");
				customer.setStatus("SUCCESS");
				return customer;
			}
			String arguement = jsonArray.getJSONObject(0).getString("i");

			log.info("-");
			// 获取详细信息
			log.info("[{}]获取详细信息", no);
			url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
			log.info("[{}]地址:{}", no, url);
			parameters.clear();
			parameters.put("__EVENTARGUMENT", arguement);
			parameters.put("__EVENTTARGET", "ctl00$_ContentPlaceHolder$selectClick");
			parameters.put("__EVENTVALIDATION", document.getElementById("__EVENTVALIDATION").val());
			parameters.put("__VIEWSTATE", document.getElementById("__VIEWSTATE").val());
			parameters.put("__VIEWSTATEENCRYPTED", document.getElementById("__VIEWSTATEENCRYPTED").val());
			parameters.put("ctl00$hdnInput", document.getElementById("hdnInput").val());
			parameters.put("ctl00$_ContentPlaceHolder$areaCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$clientCandidateId$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$confirmationNumber$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$countryCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$firstName$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$lastName$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$phoneNumber$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$postalCode$textBox", "");
			parameters.put("ctl00$_ContentPlaceHolder$prometricTestingId$textBox", no);
			parameters.put("ctl00$_ContentPlaceHolder$searchByList", "TestingID");
			parameters.put("sortByCriteria", "name");
			log.info("[{}]请求:{}", no, parameters);
			response = http.post(url, headers, parameters);

			String[] lines = response.split(System.lineSeparator());
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				log.info("[{}]响应:{}", no, line);
				if (line.contains("var a")) {
					line = line.substring(line.indexOf("{"));
					line = line.substring(0, line.length() - 1);
					JSONObject object = JSON.parseObject(line);
					JSONObject data = object.getJSONObject("data");
					if (data == null) {
						log.info("[{}]失败原因:object中不包含data", no);
						customer.setReason("object中不包含data");
						return customer;
					}
					JSONObject info = data.getJSONObject("i");
					if (info == null) {
						log.info("[{}]失败原因:data中不包含i", no);
						customer.setReason("data中不包含i");
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
			log.info("[{}]失败原因:结果中不包含object", no);
			customer.setReason("结果中不包含object");
			return customer;
		} catch (Exception e) {
			log.info("[{}]失败原因:{}", no, e.getMessage());
			customer.setReason(e.getMessage());
			return customer;
		}
	}

	private boolean documentIsLegal(String cdNo, Document document) {
		Element EVENTVALIDATION = document.getElementById("__EVENTVALIDATION");
		Element VIEWSTATE = document.getElementById("__VIEWSTATE");
		Element VIEWSTATEENCRYPTED = document.getElementById("__VIEWSTATEENCRYPTED");
		Element hdnInput = document.getElementById("hdnInput");
		if (Kit.isAnyNull(EVENTVALIDATION, VIEWSTATE, VIEWSTATEENCRYPTED, hdnInput)) {
			log.info("[{}]页面必传参数有Null", cdNo);
			return false;
		}
		return true;
	}

}
