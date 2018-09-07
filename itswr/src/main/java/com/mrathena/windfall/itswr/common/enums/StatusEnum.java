package com.mrathena.windfall.itswr.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统状态
 * @author mrathena on 2018-09-07 09:46:04.896
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

	INIT("INIT", "OkHttpClient初始化中"),
	LIVE("LIVE", "OkHttpClient维护中"),
	IDLE("IDLE", "没有爬虫任务,闲置中"),
	CRAWL("CRAWL", "执行爬虫任务中");

	private String code;
	private String desc;

	public String getDescByCode(String code) {
		if (code == null || code.isEmpty()) {
			return null;
		}
		for (StatusEnum status : StatusEnum.values()) {
			if (status.getCode().equals(code)) {
				return status.getDesc();
			}
		}
		return null;
	}

}
