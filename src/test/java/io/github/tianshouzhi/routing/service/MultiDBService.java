package io.github.tianshouzhi.routing.service;

import io.github.tianshouzhi.routing.mapper.db1.UserSelectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.github.tianshouzhi.routing.Routing;
import io.github.tianshouzhi.routing.RoutingContext;
import io.github.tianshouzhi.routing.entity.User;
import io.github.tianshouzhi.routing.entity.UserAccount;
import io.github.tianshouzhi.routing.mapper.db1.UserInsertMapper;
import io.github.tianshouzhi.routing.mapper.db2.UserAccountMapper;
import org.springframework.util.StringUtils;

/**
 * Created by tianshouzhi on 2018/7/30.
 */
public class MultiDBService {
	@Autowired
	UserInsertMapper userInsertMapper;

	@Autowired
	UserSelectMapper userSelectMapper;

	@Autowired
	UserAccountMapper userAccountMapper;

	public void testNoRouting(int userId, int userAccountId) {
		userSelectMapper.selectById(userId);
		userAccountMapper.selectById(userAccountId);
	}

	@Routing("ds1")
	public void testRouting(int userId, int userAccountId) {
		userSelectMapper.selectById(userId);
		userAccountMapper.selectById(userAccountId);
	}

	@Transactional
	public void testOnlyTransactional(User user) {
		userInsertMapper.insert(user);
		int i = 1 / 0;
	}

	@Transactional
	@Routing("ds2")
	public void testRoutingTransaction(UserAccount userAccount) {
		assert "ds2".equals(RoutingContext.getCurrentDataSource());
		assert !StringUtils.isEmpty(TransactionSynchronizationManager.getCurrentTransactionName());
		userAccountMapper.insert(userAccount);

		userSelectMapper.selectById(1);//抛出异常，找不到表
	}
}
