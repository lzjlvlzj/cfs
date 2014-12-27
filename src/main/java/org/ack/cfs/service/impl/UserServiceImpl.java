package org.ack.cfs.service.impl;


import org.ack.cfs.entity.User;
import org.ack.cfs.persist.BaseDao;
import org.ack.cfs.persist.UserDao;
import org.ack.cfs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Integer> implements
		UserService {
	
	@Autowired
	private UserDao userDaoImpl;
	
	@Override
	public BaseDao<User, Integer> getDao() {
		return userDaoImpl;
	}

}
