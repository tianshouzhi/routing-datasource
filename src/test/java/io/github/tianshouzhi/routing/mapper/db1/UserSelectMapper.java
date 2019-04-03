package io.github.tianshouzhi.routing.mapper.db1;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.github.tianshouzhi.routing.entity.User;

/**
 * Created by tianshouzhi on 2018/7/30.
 */
public interface UserSelectMapper {
	@Select("select * from user where id =#{id}")
	public User selectById(@Param("id") int id);
}
