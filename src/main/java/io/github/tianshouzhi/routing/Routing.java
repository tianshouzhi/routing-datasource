package io.github.tianshouzhi.routing;

import java.lang.annotation.*;

/**
 * Created by tianshouzhi on 2018/6/9.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Routing {
	String value();
}
