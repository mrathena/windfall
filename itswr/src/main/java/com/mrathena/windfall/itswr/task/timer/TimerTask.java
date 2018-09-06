package com.mrathena.windfall.itswr.task.timer;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TimerTask {

	@Scheduled(cron = "* * * * * ?")
	public void httpTimerTask() {
		log.info(LocalDateTime.now().toString());
	}

}
