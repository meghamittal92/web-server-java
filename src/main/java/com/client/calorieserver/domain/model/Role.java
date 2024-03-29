package com.client.calorieserver.domain.model;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Roles to control access to various endpoints.
 */
public enum Role {

	USER("USER"), USER_MANAGER("USER_MANAGER"), ADMIN("ADMIN");

	private static final Map<String, Role> ROLE_MAP;

	private final String name;

	Role(final String name) {

		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	static {
		Map<String, Role> map = new ConcurrentHashMap<String, Role>();
		for (Role instance : Role.values()) {
			map.put(instance.getName().toLowerCase(), instance);
		}
		ROLE_MAP = Collections.unmodifiableMap(map);
	}

	public static Role get(String name) {
		return ROLE_MAP.get(name.toLowerCase());
	}

	public static class RoleConstants {

		public static final String USER_VALUE = "USER";

		public static final String USER_MANAGER_VALUE = "USER_MANAGER";

		public static final String ADMIN_VALUE = "ADMIN";

	}

}
