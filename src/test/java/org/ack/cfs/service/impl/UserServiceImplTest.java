package org.ack.cfs.service.impl;

import static org.junit.Assert.fail;


import org.ack.cfs.common.Page;
import org.ack.cfs.entity.User;
import org.ack.cfs.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class UserServiceImplTest {
	
	@Autowired
	private UserService userServiceImpl;

	@Test
	public void testGetDao() {
		fail("Not yet implemented");
	}

	@Test
	public void testCount() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteById() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetList() {
		fail("Not yet implemented");
	}
	@Test
	public void testFindListByPage(){
		Page<User> page = userServiceImpl.findPage();
		System.out.println(page.toString());
	}
	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	@Test
	public void testSave() {
		User user = new User();
		user.setId(2);
		user.setLoginName("admin");
		user.setPassword("123");
		
		userServiceImpl.save(user);
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

}
