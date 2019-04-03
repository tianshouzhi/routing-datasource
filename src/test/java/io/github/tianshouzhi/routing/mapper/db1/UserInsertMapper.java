package io.github.tianshouzhi.routing.mapper.db1;

import io.github.tianshouzhi.routing.Routing;
import io.github.tianshouzhi.routing.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

/**
 * Created by tianshouzhi on 2018/7/30.
 */
@Routing("ds1")
public interface UserInsertMapper {

	@Insert("insert into user(name) values(#{name})")
	@Options(keyProperty = "id", keyColumn = "id", useGeneratedKeys = true) // 用于生成自增主键
	public int insert(User user);
}
