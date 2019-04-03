package io.github.tianshouzhi.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.tianshouzhi.routing.entity.User;
import io.github.tianshouzhi.routing.entity.UserAccount;
import io.github.tianshouzhi.routing.mapper.db1.UserInsertMapper;
import io.github.tianshouzhi.routing.mapper.db1.UserSelectMapper;
import io.github.tianshouzhi.routing.mapper.db2.UserAccountMapper;
import io.github.tianshouzhi.routing.service.MultiDBService;

/**
 * Created by tianshouzhi on 2018/7/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:routing-datasource-context.xml")
public class RoutingDataSourceTest {

	@Autowired
	UserInsertMapper userInsertMapper;

	@Autowired
	UserSelectMapper userSelectMapper;

	@Autowired
	UserAccountMapper userAccountMapper;

	@Autowired
	MultiDBService multiDBService;

	/**
	 * 1、测试类或接口级别的@Routing注解被内部方法继承
	 * UserInsertMapper接口上添加了@Routing注解指定了ds1，因此内部所有方法都可以访问db1中的user表
	 */
	@Test
	public void testInterfaceAnnotation() {
		User user = new User();
		user.setName("tianshouzhi");
		userInsertMapper.insert(user);
		System.out.println(user);
	}

	/**
	 * 测试方法级别Routing注解优先级高于类级别Routing注解：
	 * UserAccountMapper.selectById方法铜鼓@Routing注解指定了ds2，因此可以操作db2里的user_account表
	 */
	@Test
	public void testMethodAnnotation(){
		System.out.println(userAccountMapper.selectById(1));
	}

	/**
	 * 测试方法package级别声明
	 * UserAccountMapper.insert方法没有@Routing注解，且接口上没有声明注解，
	 * 但是package：io.github.tianshouzhi.routing.mapper.db2声明了数据源为ds2，因此insert可以操作db2里的user_account表
	 */
	@Test
	public void testPackage(){
		UserAccount userAccount = new UserAccount();
		userAccount.setAccount("12345678");
		userAccountMapper.insert(userAccount);
		System.out.println(userAccount);
	}

	/**
	 * 测试默认数据源
	 * UserSelectMapper接口和selectById方法上都没有@Routing注解，且没有包级别声明，需要走默认数据源ds1
	 */
	@Test
	public void testDefaultDataSource(){
		System.out.println(userSelectMapper.selectById(1));
	}

	/**
	 * MultiDBService#testNoRouting(int, int)方法没有@Routing配置
	 * 内部调用：
	 * userSelectMapper.selectById(userId);  //访问db1，走默认数据源
	 * userAccountMapper.selectById(userAccountId); //访问db2，走方法注解
	 */
	@Test
	public void testOutNoRouting(){
		multiDBService.testNoRouting(1,1);
	}

	/**
	 * MultiDBService#testRouting(int, int)方法指定了@Routing("ds1")，强制使用ds1，内部注解将会被忽略
	 * 内部调用：
	 * userSelectMapper.selectById(userId);  //访问db1，走默认数据源
	 * userAccountMapper.selectById(userAccountId); //访问db2，走方法注解 将会报错 BadSqlGrammarException Table 'db1.user_account' doesn't exist
	 */
	@Test(expected = BadSqlGrammarException.class)
	public void testOutAnnotationIgnoreInnerAnnotation() {
		int userId = 1;
		int userAccountId = 1;
		multiDBService.testRouting(userId, userAccountId);
	}

	/**
	 *测试只使用@Transactional注解，不使用@Routing注解，将会走默认数据源
	 *这将使用我们配置的默认数据源，如果访问了其他库中的表，将会报错，
	 *也就是说，使用了@Transactional注解后，也会忽略内部调用的其他方法的@Routing注解。
	 */
	@Test(expected = ArithmeticException.class)
	public void testOnlyTransactional() {
		User user = new User();
		user.setName("wangxiaoxiao");
		multiDBService.testOnlyTransactional(user);
	}

	/**
	 * 同时使用@Transactional/@Routing注解:
	 * 测试@Routing注解优先于@Transactional之前执行，spring事务管理器将使用@Routing注解中指定的数据源操作数据库：
	 * MultiDBService.testRoutingTransaction方法使用了@Routing("ds2")指定了要访问db2
	 *
	 *  @Transactional
	 * 	@Routing("ds2")
	 * 	public void testRoutingTransaction(UserAccount userAccount) {
	 * 		assert "ds2".equals(RoutingDataSourceUtil.getCurrentDataSource());
	 * 		assert !StringUtils.isEmpty(TransactionSynchronizationManager.getCurrentTransactionName());
	 * 		userAccountMapper.insert(userAccount);
	 *
	 * 		userSelectMapper.selectById(1);//抛出异常，找不到表
	 * 	}
	 * 方法内部调用了UserAccountMapper.insert插入一条数据，之后通过userSelectMapper.selectById抛出找不到表错误，测试事务自动回滚
	 * 注意观察数据库中的记录没有增加
	 */
	@Test(expected = BadSqlGrammarException.class)
	public void testRoutingTransaction(){
		UserAccount userAccount = new UserAccount();
		userAccount.setAccount("456789");
		multiDBService.testRoutingTransaction(userAccount);
	}
}
