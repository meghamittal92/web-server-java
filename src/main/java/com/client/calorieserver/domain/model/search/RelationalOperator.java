package com.client.calorieserver.domain.model.search;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum RelationalOperator {

    EQUAL("=="),
    NOT_EQUAL("!="),
    GREATER_THAN(">"),
    GREATER_THAN_EQUAL_TO(">="),
    LESS_THAN("<"),
    LESS_THAN_EQUAL_TO("=<");

    private static final Map<String, RelationalOperator> RELATIONAL_OPERATOR_MAP;
    private final String name;

    RelationalOperator(final String name) {

        this.name = name;
    }

    public String getBooleanOperatorName() {
        return this.name;
    }

    static {
        Map<String, RelationalOperator> map = new ConcurrentHashMap<String, RelationalOperator>();
        for (RelationalOperator instance : RelationalOperator.values()) {
            map.put(instance.getBooleanOperatorName().toLowerCase(), instance);
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
