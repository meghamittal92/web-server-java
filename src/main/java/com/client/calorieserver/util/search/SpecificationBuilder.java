package com.client.calorieserver.util.search;

import com.client.calorieserver.domain.exception.InvalidSearchQueryException;
import com.client.calorieserver.domain.model.search.Bracket;
import com.client.calorieserver.domain.model.search.Filter;
import com.client.calorieserver.domain.model.search.LogicalOperator;
import com.client.calorieserver.domain.model.search.RelationalOperator;
import com.google.common.base.Joiner;

import org.springframework.data.jpa.domain.Specification;


import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Generic builder class to build JPA Specification query for any provided
 * class using its Specification class.
 * @param <U>
 */
public class SpecificationBuilder<U> {

    private final List<Filter> params;
    private String searchString;
    private static final Pattern SpecCriteriaRegex = Pattern.compile("^([a-zA-Z_0-9\\-]+?)(" + Joiner.on("|")
            .join(RelationalOperator.getNames()) + ")([a-zA-Z_0-9\\-:@]+?)$");

    public SpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final SpecificationBuilder<U> with(final String key, final String operator, final Object value) {

        RelationalOperator relationalOperator = RelationalOperator.get(operator);
        if(relationalOperator != null)
        params.add(new Filter(key, relationalOperator, value));

        else
            throw new InvalidSearchQueryException(String.format("Unsuppported Operator %s:", operator));

        return this;
    }
    public final SpecificationBuilder<U> with(final Filter filter)
    {
        params.add(filter);
        return this;
    }

    public final SpecificationBuilder<U> with(final String searchString) {
        this.searchString = searchString;
        return this;
    }


    public Specification<U> build(Function<Filter, Specification<U>> converter) {


        Stack<?> postFixedExprStack = searchStringToPostFixExp(this.searchString);
        Stack<Specification<U>> specStack = new Stack<>();

        Collections.reverse((List<?>) postFixedExprStack);

        while (!postFixedExprStack.isEmpty()) {
            Object mayBeOperand = postFixedExprStack.pop();


            if (mayBeOperand instanceof Filter) {
                specStack.push(converter.apply((Filter) mayBeOperand));
            } else if (mayBeOperand instanceof LogicalOperator) {


                Specification<U> operand1 = specStack.pop();
                Specification<U> operand2 = specStack.pop();
                if (LogicalOperator.AND.equals(mayBeOperand))
                    specStack.push(Specification.where(operand1)
                            .and(operand2));
                else if (LogicalOperator.OR.equals(mayBeOperand))
                    specStack.push(Specification.where(operand1)
                            .or(operand2));
            }

        }
        final List<Specification<U>> specs = params.stream()
                .map(converter)
                .collect(Collectors.toCollection(ArrayList::new));
        Specification specification = specStack.isEmpty() ? (specs.isEmpty() ? null : Specification.where(specs.get(0))) : specStack.pop();

        for (final Filter filter : this.params) {
            specification = Specification.where(specification).and(converter.apply(filter));
        }
        return specification;

    }

    private Stack<?> searchStringToPostFixExp(final String searchString) {

        Stack<Object> postFixExpStack = new Stack<>();
        Stack<Object> stack = new Stack<>();

        if (searchString != null && !searchString.isBlank()) {
            Arrays.stream(searchString.split("\\s+")).forEach(token -> {
                if (LogicalOperator.get(token) != null) {
                    stack.push(LogicalOperator.get(token));
                } else if (Bracket.isLeftParanthesis(token)) {
                    stack.push(Bracket.get(token));
                } else if (Bracket.isRightParanthesis(token)) {
                    Bracket rightParen = Bracket.get(token);

                    while (!(stack.peek() instanceof Bracket && Bracket.isMatchingParen((Bracket) stack.peek(), rightParen)))
                        postFixExpStack.push(stack.pop());
                    stack.pop();
                } else {

                    Matcher matcher = SpecCriteriaRegex.matcher(token);
                    if (matcher.find()) {
                        postFixExpStack.push(new Filter(matcher.group(1), RelationalOperator.get(matcher.group(2)), matcher.group(3)));
                    }
                    else
                    {
                        throw new InvalidSearchQueryException(String.format("Expression %s did not match regex: %s",searchString,SpecCriteriaRegex));
                    }
                }
            });
        }

        while (!stack.isEmpty())
            postFixExpStack.push(stack.pop());

        return postFixExpStack;
    }


}
