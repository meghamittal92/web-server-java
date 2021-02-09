package com.client.calorieserver.domain.model.search;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Bracket {
    LEFT_PARANTHESIS("("),
    RIGHT_PARANTHESIS("(");

    private static final Map<String, Bracket> BRACKET_MAP;
    private final String name;

    Bracket(final String name) {

        this.name = name;
    }

    public static boolean isLeftParanthesis(String token) {

        if (LEFT_PARANTHESIS.name.equals(token))
            return true;
        return false;
    }

    public static boolean isRightParanthesis(String token) {

        if (RIGHT_PARANTHESIS.name.equals(token))
            return true;
        return false;
    }

    public static boolean isMatchingParen(Bracket leftParen, Bracket rightParen) {

        if (LEFT_PARANTHESIS.equals(leftParen) && RIGHT_PARANTHESIS.equals(rightParen))
            return true;

        return false;
    }

    public String getName() {
        return this.name;
    }

    static {
        Map<String, Bracket> map = new ConcurrentHashMap<String, Bracket>();
        for (Bracket instance : Bracket.values()) {
            map.put(instance.getName().toLowerCase(), instance);
        }
        BRACKET_MAP = Collections.unmodifiableMap(map);
    }

    public static Bracket get(String name) {
        return BRACKET_MAP.get(name.toLowerCase());
    }


}

