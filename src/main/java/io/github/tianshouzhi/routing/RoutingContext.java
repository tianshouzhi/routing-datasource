package io.github.tianshouzhi.routing;

/**
 * Created by tianshouzhi on 2018/7/30.
 */
public class RoutingContext {
	private static final ThreadLocal<String> DATASOURCES = new ThreadLocal<String>();

	public static void setDatasource(String dataSourceName) {
		DATASOURCES.set(dataSourceName);
	}

	public static String getCurrentDataSource() {
		return DATASOURCES.get();
	}

	public static void clear() {
		DATASOURCES.remove();
	}
}
