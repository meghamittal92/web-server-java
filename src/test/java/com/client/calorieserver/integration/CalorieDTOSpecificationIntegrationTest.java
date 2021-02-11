package com.client.calorieserver.integration;


import com.client.calorieserver.configuration.Constants;
import com.client.calorieserver.configuration.TestAuditingConfiguration;
import com.client.calorieserver.configuration.TestPersistenceConfiguration;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.CaloriePerDayDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.db.UserDay;
import com.client.calorieserver.domain.exception.InvalidSearchQueryException;
import com.client.calorieserver.domain.model.search.*;
import com.client.calorieserver.repository.CaloriePerDayRepository;
import com.client.calorieserver.repository.CalorieRepository;
import com.client.calorieserver.repository.UserRepository;
import com.client.calorieserver.util.CalorieDTOSpecification;
import com.client.calorieserver.util.SpecificationBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Import({TestAuditingConfiguration.class, TestPersistenceConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalorieDTOSpecificationIntegrationTest {


    @Autowired
    CalorieRepository calorieRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CaloriePerDayRepository caloriePerDayRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DateConstants.DATE_TIME_FORMAT);
    private static final LocalDateTime localDateTime1 = LocalDateTime.parse("2015-05-12T12:10:11", formatter);
    private static final LocalDateTime localDateTime2 = LocalDateTime.parse("2020-05-13T12:10:11", formatter);
    private static final String UNSUPPPORTED_OPERATION = "UnsupportedOperation";
    private static final String UNSUPPPORTED_KEY = "UnsupportedKey";


    private static final String NUM_CALORIES_GREATER_THAN_VALUE = "10";
    private static final String DATETIME_STRING = "2015-05-13T12:10:11";
    private static final String MEAL_DETAILS_LIKE = "beef";
    private static final String SPACE = " ";
    private static final String INVALID_SEARCH_STRING = "2@";

    private String SEARCH_STRING;

    private CalorieDTO calorieDTO1;
    private CalorieDTO calorieDTO2;

    @BeforeAll
    public void init() throws Exception {


        UserDTO userDTO1 = new UserDTO();

        userDTO1.setId(1L);
        userDTO1.setEmail("email1@gmail.com");
        userDTO1.setUsername("username");
        userDTO1.setPassword("password");
        userDTO1.setExpectedCaloriesPerDay(50);

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setId(2L);
        userDTO2.setEmail("email2@gmail.com");
        userDTO2.setUsername("username2");
        userDTO2.setPassword("password2");
        userDTO2.setExpectedCaloriesPerDay(100);

        userRepository.save(userDTO1);
        userRepository.save(userDTO2);

        calorieDTO1 = new CalorieDTO();
        calorieDTO1.setId(10L);
        calorieDTO1.setUserDTO(userDTO1);
        calorieDTO1.setNumCalories(40);
        calorieDTO1.setDateTime(localDateTime1);
        calorieDTO1.setMealDetails("burger");

        calorieRepository.save(calorieDTO1);

        calorieDTO2 = new CalorieDTO();
        calorieDTO2.setId(11L);
        calorieDTO2.setUserDTO(userDTO2);
        calorieDTO2.setNumCalories(500);
        calorieDTO2.setDateTime(localDateTime2);
        calorieDTO2.setMealDetails("beef");

        calorieRepository.save(calorieDTO2);

        CaloriePerDayDTO caloriePerDayDTO1 = new CaloriePerDayDTO();
        caloriePerDayDTO1.setId(new UserDay(calorieDTO1.getUserDTO().getId(), calorieDTO1.getDateTime().toLocalDate()));
        caloriePerDayDTO1.setTotalCalories(calorieDTO1.getNumCalories());

        caloriePerDayRepository.save(caloriePerDayDTO1);

        CaloriePerDayDTO caloriePerDayDTO2 = new CaloriePerDayDTO();
        caloriePerDayDTO2.setId(new UserDay(calorieDTO2.getUserDTO().getId(), calorieDTO2.getDateTime().toLocalDate()));
        caloriePerDayDTO2.setTotalCalories(calorieDTO2.getNumCalories());

        caloriePerDayRepository.save(caloriePerDayDTO2);


    }

    @Test
    public void queryWithNoContraintSuccess() {
        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();

        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
        List<CalorieDTO> results = this.calorieRepository.findAll(spec);

        Assertions.assertTrue(results.size() == 2);

    }

    @ParameterizedTest
    @MethodSource("providerNumberStringQuery")
    public void queryNumberStringSuccess(final Filter filter, final Integer resultSize) {
        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();

        specBuilder.with(filter);
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
        List<CalorieDTO> results = this.calorieRepository.findAll(spec);

        Assertions.assertTrue(results.size() == resultSize, String.format("Result Size expected %d and actual %d", resultSize, results.size()));

    }

    private static Stream<Arguments> providerNumberStringQuery() {
        return Stream.of(
                Arguments.of(new Filter(CalorieSearchKey.mealDetails.getName(), RelationalOperator.LIKE, "burger"), 1),
                Arguments.of(new Filter(CalorieSearchKey.numCalories.getName(), RelationalOperator.GREATER_THAN, 50), 1),
                Arguments.of(new Filter(CalorieSearchKey.numCalories.getName(), RelationalOperator.GREATER_THAN_EQUAL_TO, 40), 2),
                Arguments.of(new Filter(CalorieSearchKey.numCalories.getName(), RelationalOperator.LESS_THAN, 40), 0),
                Arguments.of(new Filter(CalorieSearchKey.numCalories.getName(), RelationalOperator.LESS_THAN_EQUAL_TO, 40), 1),
                Arguments.of(new Filter(CalorieSearchKey.numCalories.getName(), RelationalOperator.EQUAL, 100), 0),
                Arguments.of(new Filter(CalorieSearchKey.numCalories.getName(), RelationalOperator.NOT_EQUAL, 500), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("providerDateTimeQuery")
    public void queryDateTimeSuccess(final Filter filter, final Integer resultSize) {
        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();

        specBuilder.with(filter);
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
        List<CalorieDTO> results = this.calorieRepository.findAll(spec);

        Assertions.assertTrue(results.size() == resultSize, String.format("Result Size expected %d and actual %d", resultSize, results.size()));

    }

    private static Stream<Arguments> providerDateTimeQuery() {
        return Stream.of(
                Arguments.of(new Filter(CalorieSearchKey.dateTime.getName(), RelationalOperator.GREATER_THAN, localDateTime1), 1),
                Arguments.of(new Filter(CalorieSearchKey.dateTime.getName(), RelationalOperator.GREATER_THAN_EQUAL_TO, localDateTime1), 2),
                Arguments.of(new Filter(CalorieSearchKey.dateTime.getName(), RelationalOperator.LESS_THAN, localDateTime1), 0),
                Arguments.of(new Filter(CalorieSearchKey.dateTime.getName(), RelationalOperator.LESS_THAN_EQUAL_TO, localDateTime1), 1),
                Arguments.of(new Filter(CalorieSearchKey.dateTime.getName(), RelationalOperator.EQUAL, localDateTime2), 1),
                Arguments.of(new Filter(CalorieSearchKey.dateTime.getName(), RelationalOperator.NOT_EQUAL, localDateTime2), 1)
        );
    }


    @ParameterizedTest
    @MethodSource("providerBooleanQuery")
    public void queryBooleanSuccess(final Filter filter, final Integer resultSize) {
        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();

        specBuilder.with(filter);
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
        List<CalorieDTO> results = (List) this.calorieRepository.findAll(spec);


        Assertions.assertTrue(results.size() == resultSize, String.format("Result Size expected %d and actual %d", resultSize, results.size()));

    }

    private static Stream<Arguments> providerBooleanQuery() {
        return Stream.of(

                Arguments.of(new Filter(CalorieSearchKey.withinLimit.getName(), RelationalOperator.EQUAL, true), 1),
                Arguments.of(new Filter(CalorieSearchKey.withinLimit.getName(), RelationalOperator.EQUAL, false), 1),
                Arguments.of(new Filter(CalorieSearchKey.withinLimit.getName(), RelationalOperator.NOT_EQUAL, true), 1),
                Arguments.of(new Filter(CalorieSearchKey.withinLimit.getName(), RelationalOperator.NOT_EQUAL, false), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("providerQueryInvalidQueryException")
    public void queryInvalidQueryException(final String key, final String operator, final Object value) {
        try {
            SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();
            specBuilder.with(key, operator, value);
            Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
            this.calorieRepository.findAll(spec);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof InvalidSearchQueryException);
        }


    }

    private static Stream<Arguments> providerQueryInvalidQueryException() {
        return Stream.of(
                Arguments.of(UNSUPPPORTED_KEY, RelationalOperator.EQUAL.getName(), true),
                Arguments.of(CalorieSearchKey.dateTime.getName(), UNSUPPPORTED_OPERATION, localDateTime2),
                Arguments.of(CalorieSearchKey.numCalories.getName(), UNSUPPPORTED_OPERATION, 4)
        );
    }

    @Test
    public void queryStringSuccess() {
        SEARCH_STRING = separateTokensBySpaces(Bracket.LEFT_PARANTHESIS.getName(),
                CalorieSearchKey.numCalories.getName() + RelationalOperator.GREATER_THAN.getName() + NUM_CALORIES_GREATER_THAN_VALUE,
                LogicalOperator.AND.name(),
                CalorieSearchKey.dateTime.getName() + RelationalOperator.LESS_THAN.getName() + DATETIME_STRING,
                Bracket.RIGHT_PARANTHESIS.getName(),
                LogicalOperator.OR.name(),
                CalorieSearchKey.mealDetails.getName() + RelationalOperator.LIKE.getName() + MEAL_DETAILS_LIKE);

        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();
        specBuilder.with(SEARCH_STRING);
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
        List<CalorieDTO> results = this.calorieRepository.findAll(spec);

        Assertions.assertTrue(results.size() == 2, String.format("Result Size expected %d and actual %d", 2, results.size()));

    }

    @Test
    public void queryStringEmptySuccess() {

        SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();
        specBuilder.with("");
        Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
        List<CalorieDTO> results = this.calorieRepository.findAll(spec);

        Assertions.assertTrue(results.size() == 2, String.format("Result Size expected %d and actual %d", 2, results.size()));

    }

    @ParameterizedTest
    @MethodSource("providerQueryWithStringInvalidQueryException")
    public void queryWithStringInvalidQueryException(final String queryString) {
        try {
            SpecificationBuilder<CalorieDTO> specBuilder = new SpecificationBuilder<CalorieDTO>();
            specBuilder.with(queryString);
            Specification<CalorieDTO> spec = specBuilder.build(CalorieDTOSpecification::new);
            this.calorieRepository.findAll(spec);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof InvalidSearchQueryException);
        }


    }

    private static Stream<Arguments> providerQueryWithStringInvalidQueryException() {
        return Stream.of(
                Arguments.of(INVALID_SEARCH_STRING),
                Arguments.of(CalorieSearchKey.mealDetails.getName() + UNSUPPPORTED_OPERATION + "beef")
        );
    }

    private String separateTokensBySpaces(String... tokens) {
        final StringBuilder builder = new StringBuilder();

        for (String token : tokens) {
            builder.append(token);
            builder.append(SPACE);
        }
        return builder.toString();
    }
}
