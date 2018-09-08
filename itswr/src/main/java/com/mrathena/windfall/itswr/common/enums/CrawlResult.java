package com.mrathena.windfall.itswr.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 爬取结果
 * @author mrathena on 2018-09-08 08:54:21
 */
@Getter
@AllArgsConstructor
public enum CrawlResult {

	SUCCESS("SUCCESS", "成功"),
	FAILURE("FAILURE", "失败");

	private String code;
	private String desc;

}
