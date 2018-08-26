package com.mrathena.windfall.itswr.bo;

import java.util.List;

public class DataTables {

	private Integer draw;// draw.绘制计数器,这个是用来确保Ajax从服务器返回的是对应的,Ajax是异步的,因此返回的顺序是不确定的,要求在服务器接收到此参数后再返回
	private Integer start;// start.分页数据起始位置
	private Integer length;// length.分页数据量

	public Integer getSize() {
		return length == null ? 10 : length;
	}

	public Integer getIndex() {
		return length == null || start == null ? 1 : start / length + 1;
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer lengtn) {
		this.length = lengtn;
	}

	@Override
	public String toString() {
		return "DataTables [draw=" + draw + ", start=" + start + ", length=" + length + "]";
	}

	// DataTables返回值
	public static class Result<T> {

		private Integer draw;
		private List<T> data;
		private Long recordsTotal;
		private Long recordsFiltered;
		private String error;

		public Result(Integer draw, List<T> data, Long recordsTotal, Long recordsFiltered) {
			super();
			this.draw = draw;
			this.data = data;
			this.recordsTotal = recordsTotal;
			this.recordsFiltered = recordsFiltered;
		}

		public Result(Integer draw, String error) {
			super();
			this.draw = draw;
			this.data = null;
			this.recordsTotal = 0L;
			this.recordsFiltered = 0L;
			this.error = error;
		}

		public Integer getDraw() {
			return draw;
		}

		public void setDraw(Integer draw) {
			this.draw = draw;
		}

		public List<T> getData() {
			return data;
		}

		public void setData(List<T> data) {
			this.data = data;
		}

		public Long getRecordsTotal() {
			return recordsTotal;
		}

		public void setRecordsTotal(Long recordsTotal) {
			this.recordsTotal = recordsTotal;
		}

		public Long getRecordsFiltered() {
			return recordsFiltered;
		}

		public void setRecordsFiltered(Long recordsFiltered) {
			this.recordsFiltered = recordsFiltered;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		@Override
		public String toString() {
			return "Result [draw=" + draw + ", data=" + data + ", recordsTotal=" + recordsTotal + ", recordsFiltered="
					+ recordsFiltered + ", error=" + error + "]";
		}

	}
}