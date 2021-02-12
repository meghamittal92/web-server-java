package com.client.calorieserver.domain.model.search;


import com.client.calorieserver.domain.dto.db.UserDTO_;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Search keys for search operation on Users.
 */
public enum UserSearchKey {

    username("username", UserDTO_.USERNAME),
    email("email", UserDTO_.EMAIL),
    expectedCaloriesPerDay("expectedCaloriesPerDay", UserDTO_.EXPECTED_CALORIES_PER_DAY);

    private static final Map<String, UserSearchKey> USER_SEARCH_KEY_MAP;
    private final String name;
    private final String dbColumnName;

    UserSearchKey(final String name, final String dbColumnName) {

        this.name = name;
        this.dbColumnName = dbColumnName;
    }

    public String getName() {
        return name;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }


    static {
        Map<String, UserSearchKey> map = new ConcurrentHashMap<String, UserSearchKey>();
        for (UserSearchKey instance : UserSearchKey.values()) {
            map.put(instance.getName().toLowerCase(), instance);
        }
        USER_SEARCH_KEY_MAP = Collections.unmodifiableMap(map);
    }

    public static UserSearchKey get(String name) {
        return USER_SEARCH_KEY_MAP.get(name.toLowerCase());
    }


}
