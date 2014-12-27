/**
 *
 *  This is a part of the Bluedot system.
 *  Copyright (C) 2008-2009 Bluedot Corporation
 *  All rights reserved.
 *
 *  Licensed under the Bluedot private License.
 *  Created on 2010-1-4
 *  @author Administrator
**/

package org.ack.cfs.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page<T> implements Serializable {
	private static final long serialVersionUID = -5163659152602274753L;
	private List<T> results = new ArrayList<T>();	//集合
	private int currentPage;	//当前页
	private int totalPage;		//总页数
	private int totalRecord;	//总记录数
	private int pageSize = 3;   //每页显示多少条记录
	private Map<String,Object> map = new HashMap<String,Object>();//其他参数
	
	public Page(){
		  
	}
	/** 
	 * @param lists
	 * @param currentPage
	 * @param totalPage
	 * @param totalRecord
	 */
	public Page(List<T> results,int currentPage,int totalPage,int totalRecord){
		this.results = results;
		this.currentPage = currentPage;
		this.totalPage = totalPage;
		this.totalRecord = totalRecord;
	}

	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public List<?> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	public String toString() {
		
		return "当前>>"+currentPage+"总页数>>>"+totalPage+"结果集:"+results;
	}
	
}
