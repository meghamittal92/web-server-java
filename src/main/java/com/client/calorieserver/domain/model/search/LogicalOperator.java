package com.client.calorieserver.domain.model.search;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logical operators for search queries.
 */
public enum LogicalOperator {

	AND("AND"), OR("OR");

	private static final Map<String, LogicalOperator> LOGICAL_OPERATOR_MAP;

	private final String name;

	LogicalOperator(final String name) {

		this.name = name;
	}

	public String getBooleanOperatorName() {
		return this.name;
	}

	static {
		Map<String, LogicalOperator> map = new ConcurrentHashMap<String, LogicalOperator>();
		for (LogicalOperator instance : LogicalOperator.values()) {
			map.put(instance.getBooleanOperatorName().toLowerCase(), instance);
		}
		LOGICAL_OPERATOR_MAP = Collections.unmodifiableMap(map);
	}

	public static LogicalOperator get(String name) {
		return LOGICAL_OPERATOR_MAP.get(name.toLowerCase());
	}

}
