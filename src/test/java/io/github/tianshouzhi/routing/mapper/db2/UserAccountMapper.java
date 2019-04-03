package io.github.tianshouzhi.routing.mapper.db2;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.github.tianshouzhi.routing.Routing;
import io.github.tianshouzhi.routing.entity.UserAccount;

/**
 * Created by tianshouzhi on 2018/7/30.
 */
public interface UserAccountMapper {
	@Select("select * from user_account where id = #{id}")
	@Routing("ds2")
	UserAccount selectById(@Param("id") int id);

	@Insert("insert into user_account(account) values(#{account})") // 用于生成自增主键
	@Options(keyProperty = "id", keyColumn = "id", useGeneratedKeys = true)
	int insert(UserAccount userAccount);
}
