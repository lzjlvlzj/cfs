package org.ack.cfs.persist.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.Resource;

import org.ack.cfs.common.Page;
import org.ack.cfs.persist.BaseDao;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

public abstract class BaseDaoImpl<T extends Object, PK extends Serializable> extends
		SqlSessionDaoSupport implements BaseDao<T, PK> {
	
	public static final String SQLNAME_SEPARATOR = ".";

	public static final String SQL_SAVE = "insert";   
	public static final String SQL_UPDATE = "update";   
	public static final String SQL_GETBYID = "getById";
	public static final String SQL_DELETEBYID = "deleteById";
	public static final String SQL_DELETEBYIDS = "deleteByIds";
	public static final String SQL_FINDPAGEBY = "findPageList";   
	public static final String SQL_FINDLISTBY = "findListBy";
	public static final String SQL_GETCOUNTBY = "getCountBy";

	public static final String SORT_NAME = "SORT";
	
	public static final String DIR_NAME = "DIR";
	public static final String[] ILLEGAL_CHARS_FOR_SQL = {",", ";", " ", "\"", "%"};
	
	//private String sqlNamespace = null;//
	
	private Class<T> clazz;
	
	//1.2.0 取消自动注入 SqlSessionFactory or SqlSessionTemplate 需要手动
	@Resource
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
	         super.setSqlSessionFactory(sqlSessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public BaseDaoImpl(){
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass(); 
		this.clazz = (Class<T>) pt.getActualTypeArguments()[0]; //	
		System.out.println("clazz ---> " + clazz.toString());
	}
	
	/**
	 * @return 命名空间
	 */
	public abstract String getNameSpace();
	
	/**
	 * @param sqlName
	 * @return
	 */
	protected String getSqlName(String sqlName) {
		String sqlNamespace = getNameSpace();
		return  sqlNamespace + SQLNAME_SEPARATOR + sqlName;
	}

	public Integer insert(T entity) {
		int result = -1;
		String sql = getSqlName(SQL_SAVE);
		result = getSqlSession().insert(sql, entity);
		return result;
	}

	public Integer update(T entity) {

		return 0;
	}

	public Integer deleteById(PK k) {
		return 0;
	}

	public Integer delete(T entity) {
		return 0;
	}

	public T findById(PK k) {
		return null;
	}

	public List<T> getByIds(PK[] ids) {
		return null;
	}

	public List<T> findAll() {
		return null;
	}
	
	public List<T> findPageList(Page<T> page){
		String sql = getSqlName(SQL_FINDPAGEBY);
		return getSqlSession().selectList(sql,page);
	}

	public int count() {
		return 0;
	}

	public void deleteAll() {

	}

}
