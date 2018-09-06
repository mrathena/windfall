package com.mrathena.windfall.itswr.task.initialize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mrathena.windfall.itswr.common.Constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(value = Constant.INTEGER_1)
public class HttpInitTask implements ApplicationRunner {

	@Autowired
	private CacheManager cache;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("初始化任务开始:请求客户端*100");
		cache.getCache("https").put("key", "你好");
	}

}
