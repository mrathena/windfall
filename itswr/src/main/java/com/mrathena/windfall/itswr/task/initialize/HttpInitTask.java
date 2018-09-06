package com.mrathena.windfall.itswr.task.initialize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class HttpInitTask implements ApplicationRunner {

	@Autowired
	private CacheManager cache;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("初始化任务");
		System.out.println(cache.getCache("https"));
		System.out.println(cache.getCacheNames());
	}

}
