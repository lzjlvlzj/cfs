package org.ack.cfs.service.impl;

import java.io.Serializable;
import java.util.List;

import org.ack.cfs.common.Page;
import org.ack.cfs.persist.BaseDao;
import org.ack.cfs.service.BaseService;

public abstract class BaseServiceImpl<T extends Object, PK extends Serializable> implements BaseService<T,PK>{

	/**
	 * 用户实现
	 * */
	public abstract BaseDao<T, PK> getDao();

	public int count() {
		return getDao().count();
	}

	public void delete(T t) {
		getDao().delete(t);
	}

	public void deleteAll() {
		getDao().deleteAll();
	}

	public void deleteById(PK id) {
		getDao().deleteById(id);
	}

	public List<T> getList() {
		return getDao().findAll();
	}
	public Page<T> findPage(){
		Page<T> page = new Page<T>();
		page.setCurrentPage(1);
		List<T> list = getDao().findPageList(page);
		page.setResults(list);
		return page;
	}
	public T load(PK id) {
		return null;
	}

	public void save(T t) {
		getDao().insert(t);
	}

	public void update(T t) {
		getDao().update(t);
	}
}
