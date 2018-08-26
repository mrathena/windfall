package com.mrathena.windfall.itswr.bo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Progress {

	// 总数
	private long total;
	// 当前已结束数量
	private long count;

	private String startNo;
	private String stopNo;
	private String status;

	private List<String> success = new ArrayList<>();
	private List<String> failure = new ArrayList<>();

	public Progress(String status) {
		this.status = status;
	}

	public Progress(String status, long total) {
		this.status = status;
		this.total = total;
	}

}
