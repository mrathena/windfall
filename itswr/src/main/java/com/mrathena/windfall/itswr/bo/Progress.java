package com.mrathena.windfall.itswr.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Progress {

	private String status;

	// 总数
	private long total;
	// 已成功数量
	private long success;
	// 已失败数量
	private long failure;

	public Progress(String status) {
		this.status = status;
	}

	public Progress(String status, long total) {
		this.status = status;
		this.total = total;
	}

}
