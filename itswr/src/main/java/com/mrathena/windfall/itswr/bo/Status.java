package com.mrathena.windfall.itswr.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Status {

	private String status;
	private InitStatus init;
	private LiveStatus live;
	private CrawlStatus crawl;

	public Status(String status) {
		this.status = status;
	}

	public Status init(InitStatus init) {
		this.init = init;
		return this;
	}

	public Status live(LiveStatus live) {
		this.live = live;
		return this;
	}

	public Status crawl(CrawlStatus crawl) {
		this.crawl = crawl;
		return this;
	}

	/**
	 * Http对象初始化任务状态
	 */
	@Getter
	@Setter
	@ToString
	public static class InitStatus {
		// 需要初始化的Http对象的数量
		private long total;
		// 初始化流程中完成第一步的数量(Get方式请求登录页)
		private long first;
		// 初始化流程中完成第二步的数量(Post方式执行登录操作)
		private long second;
		// 初始化流程中完成第三步(最后一步)且成功的数量(Post方式选择CN52节点)
		private long success;
		// 初始化流程中失败的数量(三个步骤中任何一个步骤失败)
		private long failure;

		public InitStatus(long total) {
			this.total = total;
		}

		public boolean isSuccess() {
			return total == success;
		}
	}

	/**
	 * 爬取任务状态
	 */
	@Getter
	@Setter
	@ToString
	public static class CrawlStatus {
		// 爬取数量
		private long total;
		// 爬取成功的数量
		private long success;
		// 爬取失败的数量
		private long failure;

		public CrawlStatus(long total) {
			this.total = total;
		}
	}

	/**
	 * Http对象保活任务状态
	 */
	@Getter
	@Setter
	@ToString
	public static class LiveStatus {
		// 需要保活的Http对象的数量
		private long total;
		// 保活成功的数量
		private long success;
		// 保活失败的数量
		private long failure;

		public LiveStatus(long total) {
			this.total = total;
		}

		public boolean isSuccess() {
			return total == success;
		}
	}

}
