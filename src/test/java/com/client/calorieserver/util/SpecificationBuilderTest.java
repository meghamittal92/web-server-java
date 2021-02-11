//package com.client.calorieserver.util;
//
//import com.client.calorieserver.domain.dto.db.CalorieDTO;
//import com.client.calorieserver.domain.model.search.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.function.Function;
//
//public class SpecificationBuilderTest {
//
//    private static final String KEY = "key";
//    private static final String OBJECT_TO_COMPARE = "key";
//    private static final String NUM_CALORIES_GREATER_THAN_VALUE = "10";
//    private static final String DATETIME_STRING = "2015-05-12T12:10:11";
//    private static final String MEAL_DETAILS_LIKE = "burger";
//    private static final String SPACE = " ";
//
//    private String SEARCH_STRING;
//
//    @Mock
//    Function<Filter, Specification<CalorieDTO>> converter;
//
//    @Mock
//    Specification<CalorieDTO> specification;
//    SpecificationBuilder<CalorieDTO> specificationBuilder;
//
//
//    @BeforeEach
//    void init()
//    {
//        MockitoAnnotations.openMocks(this);
//        specificationBuilder = new SpecificationBuilder<CalorieDTO>();
//        SEARCH_STRING = separateTokensBySpaces(Bracket.LEFT_PARANTHESIS.getName(),
//                CalorieSearchKey.numCalories.getName() + RelationalOperator.GREATER_THAN.getName() + NUM_CALORIES_GREATER_THAN_VALUE ,
//                LogicalOperator.AND.name(),
//                CalorieSearchKey.dateTime.getName() + RelationalOperator.LESS_THAN.getName() + DATETIME_STRING,
//                Bracket.RIGHT_PARANTHESIS.getName(),
//                LogicalOperator.OR.name(),
//                CalorieSearchKey.mealDetails.getName() + RelationalOperator.LIKE.getName() + MEAL_DETAILS_LIKE);
//    }
//
//    private String separateTokensBySpaces(String ... tokens)
//    {
//        final StringBuilder builder = new StringBuilder();
//
//        for(String token : tokens)
//        {
//            builder.append(token);
//            builder.append(SPACE);
//        }
//        return builder.toString();
//    }
//
//
//    @Test
//    public void buildWithOnlySearchString()
//    {
//        //Mockito.when(converter.apply(Mockito.any(Filter.class))).thenReturn(specification);
//        final Specification<CalorieDTO>  builtSpecification = specificationBuilder.with(SEARCH_STRING).build(converter);
//        System.out.println(builtSpecification);
//
//    }
//}
