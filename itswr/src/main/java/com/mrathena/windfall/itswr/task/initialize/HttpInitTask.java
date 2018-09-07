package com.mrathena.windfall.itswr.task.initialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.mrathena.windfall.itswr.bo.Status;
import com.mrathena.windfall.itswr.bo.Status.InitStatus;
import com.mrathena.windfall.itswr.common.constant.BusinessConstant;
import com.mrathena.windfall.itswr.common.constant.Constant;
import com.mrathena.windfall.itswr.common.enums.StatusEnum;
import com.mrathena.windfall.itswr.tool.Http;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(value = Constant.INT_1)
public class HttpInitTask implements ApplicationRunner {

	@Autowired
	private Environment environment;

	@Autowired
	private CacheManager cacheManager;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info(Constant.EMPTY);
		log.info("初始化任务开始");

		Cache cache = cacheManager.getCache(BusinessConstant.CACHE);
		InitStatus initStatus = new InitStatus(BusinessConstant.THREAD_COUNT);
		Status status = new Status(StatusEnum.INIT.getCode()).init(initStatus);
		cache.put(BusinessConstant.STATUS, status);

		// 初始化Http对象集合
		// 获取itswr账号密码
		String username = environment.getProperty(BusinessConstant.USERNAME_KEY);
		String password = environment.getProperty(BusinessConstant.PASSWORD_KEY);

		// 请求Headers
		Map<String, String> headers = new HashMap<>();
		headers.put(BusinessConstant.USER_AGENT, BusinessConstant.USER_AGENT_VALUE);
		headers.put(BusinessConstant.COOKIE, BusinessConstant.COOKIE_VALUE);

		// Http对象集合
		List<Http> https = new ArrayList<>(BusinessConstant.THREAD_COUNT);
		for (int i = 1; i <= BusinessConstant.THREAD_COUNT; i++) {
			https.add(Http.getInstance());
		}

		// 计数器
		AtomicInteger firstCounter = new AtomicInteger(Constant.INT_0);
		AtomicInteger secondCounter = new AtomicInteger(Constant.INT_0);
		AtomicInteger successCounter = new AtomicInteger(Constant.INT_0);
		AtomicInteger failureCounter = new AtomicInteger(Constant.INT_0);

		// 线程池
		ExecutorService executor = Executors.newFixedThreadPool(BusinessConstant.THREAD_COUNT);
		https.forEach(http -> {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						// Http对象鉴权
						Map<String, Object> parameters = new HashMap<>();

						// 获取登录页面
						String url = "https://itswr.prometric.com/login.aspx";
						String response = http.get(url, headers, null);
						Document document = Jsoup.parse(response);
						initStatus.setFirst(firstCounter.incrementAndGet());

						// 登录(获取SecurityServices)
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
						response = http.post(url, headers, parameters);
						document = Jsoup.parse(response);
						initStatus.setSecond(secondCounter.incrementAndGet());

						// 选择结点(CN52N)
						url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
						parameters.clear();
						parameters.put("__EVENTARGUMENT", "click-0");
						parameters.put("__EVENTTARGET", "ctl00$_HeaderPlaceHolder$siteDropdown");
						parameters.put("__EVENTVALIDATION", document.getElementById("__EVENTVALIDATION").val());
						parameters.put("__VIEWSTATE", document.getElementById("__VIEWSTATE").val());
						parameters.put("__VIEWSTATEENCRYPTED", document.getElementById("__VIEWSTATEENCRYPTED").val());
						parameters.put("ctl00$hdnInput", document.getElementById("hdnInput").val());
						response = http.post(url, headers, parameters);
						document = Jsoup.parse(response);
						initStatus.setSuccess(successCounter.incrementAndGet());
						// 如有必要可加一下结果判断
					} catch (Exception e) {
						log.error(Constant.EMPTY, e);
						initStatus.setFailure(failureCounter.incrementAndGet());
					}
				}
			});
		});
		executor.shutdown();

		boolean loop = true;
		do { // 等待所有任务完成
			loop = !executor.awaitTermination(1, TimeUnit.SECONDS); // 阻塞，直到线程池里所有任务结束
		} while (loop);

		status.setStatus(StatusEnum.IDLE.getCode());
		cache.put(BusinessConstant.HTTPS, https);

		String result = initStatus.isSuccess() ? "成功" : "失败";
		long total = initStatus.getTotal();
		long first = initStatus.getFirst();
		long second = initStatus.getSecond();
		long success = initStatus.getSuccess();
		long failure = initStatus.getFailure();
		log.info("初始化任务结束, 结果:{}, 总数:{}, 第一步成功:{}, 第二步成功:{}, 最终成功:{}, 失败:{}", result, total, first, second, success, failure);
	}

}
