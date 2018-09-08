package com.mrathena.windfall.itswr.task.timer;

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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mrathena.windfall.itswr.bo.Status;
import com.mrathena.windfall.itswr.bo.Status.InitStatus;
import com.mrathena.windfall.itswr.bo.Status.LiveStatus;
import com.mrathena.windfall.itswr.common.constant.BusinessConstant;
import com.mrathena.windfall.itswr.common.constant.Constant;
import com.mrathena.windfall.itswr.common.enums.SysemStatus;
import com.mrathena.windfall.itswr.tool.Http;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TimerTask {

	@Autowired
	private CacheManager cacheManager;

	/**
	 * 初始化进度监控定时任务
	 */
	@Scheduled(cron = "* * * * * ?")
	public void initStatusMonitorTask() {
		Cache cache = cacheManager.getCache(BusinessConstant.CACHE);
		if (cache != null) {
			Status status = cache.get(BusinessConstant.STATUS, Status.class);
			if (status != null) {
				if (SysemStatus.INIT.getCode().equals(status.getStatus())) {
					InitStatus initStatus = status.getInit();
					if (initStatus != null) {
						long total = initStatus.getTotal();
						long first = initStatus.getFirst();
						long second = initStatus.getSecond();
						long success = initStatus.getSuccess();
						long failure = initStatus.getFailure();
						log.info("初始化任务进度: 总数:{}, 第一步成功:{}, 第二步成功:{}, 最终成功:{}, 失败:{}", total, first, second, success, failure);
					}
				}
			}
		}
	}

	/**
	 * Http对象保活定时任务
	 */
	@Scheduled(cron = "0 * * * * ?")
	public void httpLiveTimerTask() throws Exception {
		Cache cache = cacheManager.getCache(BusinessConstant.CACHE);
		if (cache != null) {
			Status status = cache.get(BusinessConstant.STATUS, Status.class);
			if (status != null) {
				if (SysemStatus.INIT.getCode().equals(status.getStatus())) {
					// 当前正在执行初始化任务
					return;
				}
				if (SysemStatus.CRAWL.getCode().equals(status.getStatus())) {
					// 当前正在执行爬虫任务
					return;
				}
				if (SysemStatus.LIVE.getCode().equals(status.getStatus())) {
					// 当前正在执行保活任务
					return;
				}
				if (SysemStatus.IDLE.getCode().equals(status.getStatus())) {
					// 当前空闲,可执行保活任务
					LiveStatus liveStatus = new LiveStatus(BusinessConstant.THREAD_COUNT);
					status.live(liveStatus).setStatus(SysemStatus.LIVE.getCode());

					// 请求Headers
					Map<String, String> headers = new HashMap<>();
					headers.put(BusinessConstant.USER_AGENT, BusinessConstant.USER_AGENT_VALUE);
					headers.put(BusinessConstant.COOKIE, BusinessConstant.COOKIE_VALUE);

					// Http对象集合
					@SuppressWarnings("unchecked")
					List<Http> https = cache.get(BusinessConstant.HTTPS, List.class);

					// 计数器
					AtomicInteger successCounter = new AtomicInteger(Constant.INTEGER_0);
					AtomicInteger failureCounter = new AtomicInteger(Constant.INTEGER_0);

					// 线程池
					ExecutorService executor = Executors.newFixedThreadPool(BusinessConstant.THREAD_COUNT);
					https.forEach(http -> {
						executor.submit(new Runnable() {
							@Override
							public void run() {
								try {
									// 获取搜索页面
									String url = "https://itswr.prometric.com/SiteScheduler/Default.aspx";
									String response = http.get(url, headers, null);
									Document document = Jsoup.parse(response);
									if (document.getElementById("_ContentPlaceHolder_login_Password") == null) {
										liveStatus.setSuccess(successCounter.incrementAndGet());
									} else {
										liveStatus.setFailure(failureCounter.incrementAndGet());
									}
								} catch (Exception e) {
									log.error(Constant.EMPTY, e);
									liveStatus.setFailure(failureCounter.incrementAndGet());
								}
							}
						});
					});
					executor.shutdown();

					boolean loop = true;
					do { // 等待所有任务完成
						loop = !executor.awaitTermination(1, TimeUnit.SECONDS); // 阻塞，直到线程池里所有任务结束
					} while (loop);

					status.setStatus(SysemStatus.IDLE.getCode());

					String result = liveStatus.isSuccess() ? "成功" : "失败";
					long total = liveStatus.getTotal();
					long success = liveStatus.getSuccess();
					long failure = liveStatus.getFailure();
					log.info("定时保活任务: 结果:{}, 总数:{}, 成功:{}, 失败:{}", result, total, success, failure);
				}
			}
		}
	}

}
