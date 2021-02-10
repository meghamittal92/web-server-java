package com.client.calorieserver.domain.model.search;

import com.client.calorieserver.domain.dto.db.CalorieDTO_;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum CalorieSearchKey {

    userId("userId", CalorieDTO_.USER_ID),
    dateTime("dateTime", CalorieDTO_.DATE_TIME),
    numCalories("numCalories", CalorieDTO_.NUM_CALORIES),
    mealDetails("mealDetails", CalorieDTO_.MEAL_DETAILS),
    withinLimit("withinLimit", "");

    private static final Map<String, CalorieSearchKey> CALORIE_SEARCH_KEY_MAP;
    private final String name;
    private final String dbColumnName;

    CalorieSearchKey(final String name, final String dbColumnName) {

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
        Map<String, CalorieSearchKey> map = new ConcurrentHashMap<String, CalorieSearchKey>();
        for (CalorieSearchKey instance : CalorieSearchKey.values()) {
            map.put(instance.getName().toLowerCase(), instance);
        }
        CALORIE_SEARCH_KEY_MAP = Collections.unmodifiableMap(map);
    }

    public static CalorieSearchKey get(String name) {
        return CALORIE_SEARCH_KEY_MAP.get(name.toLowerCase());
    }


}

