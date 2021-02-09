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

    public Filter(String key, String operator, Object value) {
        this.key = key;
        //TO DO add null check here
        this.operator = RelationalOperator.get(operator);

        this.value = value;

    }
}
