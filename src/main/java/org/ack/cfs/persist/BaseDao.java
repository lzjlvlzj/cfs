package org.ack.cfs.persist;

import java.io.Serializable;
import java.util.List;

import org.ack.cfs.common.Page;


public interface BaseDao <T extends Object, PK extends Serializable>{
	/**
	 * 
	 * 
	 * @param entity
	 */
	public Integer insert(T entity);

	/**
	 * 
	 * 
	 * @param entity
	 */
	public Integer update(T entity);

	/**
	 *
	 * 
	 * @param k
	 */
	public Integer deleteById(PK k);

	/**
	 *
	 * 
	 * @param entity
	 */
	public Integer delete(T entity);
	/**
	 * 
	 * @param k
	 * @return T
	 */
	public T findById(PK k);
	/**
	 * 
	 * @param ids
	 * @return List<T>
	 */
	List<T> getByIds(PK[] ids);

	/**
	 * 
	 * 
	 * @return
	 */
	List<T> findAll();
	
	/**
	 * @return
	 */
	List<T> findPageList(Page<T> page);
	
	
	/**
	 * @return int
	 */
	int count();

	/**
	 * 
	 */
	void deleteAll();
}
