package com.mrathena.windfall.itswr.task.timer;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TimerTask {

	@Autowired
	private CacheManager cache;

	@Scheduled(cron = "* * * * * ?")
	public void httpTimerTask() {
		log.info(LocalDateTime.now().toString());
		String value = cache.getCache("https").get("key", String.class);
		log.info("Cache:{}", value);
	}

}
