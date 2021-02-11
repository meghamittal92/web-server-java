package com.client.calorieserver.domain.model.search;

import lombok.Data;

@Data
public class Filter {

    private String key;
    private RelationalOperator operator;
    private Object value;


    public Filter(String key, RelationalOperator operator, Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

}
