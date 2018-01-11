package com.wins.shop.entity;

public class PageEntity {

	public final static String TOTAL = "total" ;
	public final static Long EXPIRE = 30L ;
	
	public transient Integer total ;
	public transient Integer page ;
	public transient Integer pageSize = 3000;
	public transient Integer index = 0;
	
//	private transient Date createTimeStart ;
//	private transient Date createTimeEnd ;
//	
//	private transient Date statusTimeStart ;
//	private transient Date statusTimeEnd ;
	
	
//	public Date getCreateTimeStart() {
//		return createTimeStart;
//	}
//
//	public void setCreateTimeStart(Date createTimeStart) {
//		this.createTimeStart = createTimeStart;
//	}
//
//	public Date getCreateTimeEnd() {
//		return createTimeEnd;
//	}
//
//	public void setCreateTimeEnd(Date createTimeEnd) {
//		this.createTimeEnd = createTimeEnd;
//	}
//
//	public Date getStatusTimeStart() {
//		return statusTimeStart;
//	}
//
//	public void setStatusTimeStart(Date statusTimeStart) {
//		this.statusTimeStart = statusTimeStart;
//	}
//
//	public Date getStatusTimeEnd() {
//		return statusTimeEnd;
//	}
//
//	public void setStatusTimeEnd(Date statusTimeEnd) {
//		this.statusTimeEnd = statusTimeEnd;
//	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
	
	
//	private Short oper ; // 0 - 新增; 1 - 修改
//	public Short getOper() {
//		return oper;
//	}
//
//	public void setOper(Short oper) {
//		this.oper = oper;
//	}

	
}
