package io.github.tianshouzhi.routing;

import io.github.tianshouzhi.routing.mapper.db1.UserSelectMapper;
import io.github.tianshouzhi.routing.service.MultiDBService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.tianshouzhi.routing.mapper.db1.UserInsertMapper;
import io.github.tianshouzhi.routing.mapper.db2.UserAccountMapper;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:routing-datasource-context.xml")
public class AopTest {
	@Autowired
	UserInsertMapper userInsertMapper;

	@Autowired
	UserSelectMapper userSelectMapper;

	@Autowired
	UserAccountMapper userAccountMapper;

	@Autowired
	MultiDBService multiDBService;

	@Test
	public void testMethod() {
		assert userAccountMapper instanceof Advised;
	}

	@Test
	public void testClass() {
		assert userInsertMapper instanceof Advised;
//		assert userSelectMapper instanceof Advised;
	}

	@Test
	public void testSecondProxy() throws Exception {
		assert multiDBService instanceof Advised;
		Advisor[] advisors = ((Advised) multiDBService).getAdvisors();
		assert advisors[0].getAdvice() instanceof RoutingAdvice;
		assert advisors[1].getAdvice() instanceof TransactionInterceptor;
	}
}
