package com.client.calorieserver.domain.model.search;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Relational operators for search queries.
 */
public enum RelationalOperator {

	EQUAL("=="), NOT_EQUAL("!="), GREATER_THAN(">"), GREATER_THAN_EQUAL_TO(">="), LESS_THAN("<"), LESS_THAN_EQUAL_TO(
			"<="), LIKE("~");

	private static final Map<String, RelationalOperator> RELATIONAL_OPERATOR_MAP;

	private final String name;

	RelationalOperator(final String name) {

		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	static {
		Map<String, RelationalOperator> map = new ConcurrentHashMap<String, RelationalOperator>();
		for (RelationalOperator instance : RelationalOperator.values()) {
			map.put(instance.getName().toLowerCase(), instance);
		}
		RELATIONAL_OPERATOR_MAP = Collections.unmodifiableMap(map);
	}

	public static RelationalOperator get(String name) {
		return RELATIONAL_OPERATOR_MAP.get(name.toLowerCase());
	}

	public static Set<String> getNames() {
		return RELATIONAL_OPERATOR_MAP.keySet();
	}

}
