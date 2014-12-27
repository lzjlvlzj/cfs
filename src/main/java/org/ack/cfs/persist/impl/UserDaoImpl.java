package org.ack.cfs.persist.impl;



import org.ack.cfs.entity.User;
import org.ack.cfs.persist.UserDao;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends BaseDaoImpl<User, Integer> implements UserDao {

	@Override
	public String getNameSpace() {
		return this.getClass().getName();
	}
	
	
}
