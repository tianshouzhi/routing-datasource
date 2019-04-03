package io.github.tianshouzhi.routing;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by tianshouzhi on 2018/8/3.
 */
public class RoutingPointcut implements Pointcut, MethodMatcher {

	private Map<String, String> packageDataSourceKeyMap = null;

	RoutingPointcut(Map<String, String> packageDataSourceKeyMap) {
		this.packageDataSourceKeyMap = packageDataSourceKeyMap;
	}

	// ---------------------------------------------------------------------
	// Pointcut methods
	// ---------------------------------------------------------------------
	@Override
	public ClassFilter getClassFilter() {
		return ClassFilter.TRUE;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return this;
	}

	// ---------------------------------------------------------------------
	// MethodMatcher methods
	// ---------------------------------------------------------------------
	@Override
	public boolean isRuntime() {
		return false;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return match(method, targetClass);
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object... args) {
		return match(method, targetClass);
	}

	boolean match(Method method, Class<?> targetClass) {
		// origin
		if (doMatch(method, targetClass)) {
			return true;
		}

		// cglib
		Class<?> userClass = ClassUtils.getUserClass(targetClass);
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		if (!specificMethod.equals(method)) {
			if (doMatch(specificMethod, userClass)) {
				return true;
			}
		}

		// jdk proxy
		if (Proxy.isProxyClass(targetClass)) {
			Class<?>[] interfaces = targetClass.getInterfaces();
			for (Class<?> interfaceClass : interfaces) {
				Method interfaceMethod = ClassUtils.getMethodIfAvailable(interfaceClass, method.getName(),
				      method.getParameterTypes());
				if (interfaceMethod != null && doMatch(interfaceMethod, interfaceClass)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean doMatch(Method method, Class<?> targetClass) {
		if (method.isAnnotationPresent(Routing.class) || targetClass.isAnnotationPresent(Routing.class)
				|| method.getDeclaringClass().isAnnotationPresent(Routing.class)) {
			return true;
		}
		if (packageDataSourceKeyMap != null) {
			for (String _package : packageDataSourceKeyMap.keySet()) {
				if (targetClass.getName().startsWith(_package)
						|| method.getDeclaringClass().getName().startsWith(_package)) {
					return true;
				}
			}
		}
		return false;
	}
}
