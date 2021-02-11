package com.client.calorieserver.integration;

import com.client.calorieserver.configuration.TestAuditingConfiguration;
import com.client.calorieserver.configuration.TestPersistenceConfiguration;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.exception.InvalidSearchQueryException;
import com.client.calorieserver.domain.model.search.*;
import com.client.calorieserver.repository.UserRepository;
import com.client.calorieserver.util.CalorieDTOSpecification;
import com.client.calorieserver.util.SpecificationBuilder;
import com.client.calorieserver.util.UserDTOSpecification;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Import({TestAuditingConfiguration.class, TestPersistenceConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql("/testdata/clean_all_tables.sql")
public class UserDTOSpecificationIntegrationTest {


    private static final String UNSUPPPORTED_KEY = "UnsupportedKey";
    @Autowired
    UserRepository userRepository;


    @BeforeEach
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

    }

    @AfterAll
    public void cleanDb()
    {
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("providerQuerySuccess")
    public void querySuccess(final Filter filter, final Integer resultSize) {
        SpecificationBuilder<UserDTO> specBuilder = new SpecificationBuilder<UserDTO>();

        specBuilder.with(filter);
        Specification<UserDTO> spec = specBuilder.build(UserDTOSpecification::new);
        List<UserDTO> results = (List)this.userRepository.findAll(spec);

        Assertions.assertTrue(results.size() == resultSize, String.format("Result Size expected %d and actual %d", resultSize, results.size()));

    }

    private static Stream<Arguments> providerQuerySuccess() {
        return Stream.of(
                Arguments.of(new Filter(UserSearchKey.username.getName(), RelationalOperator.LIKE, "username2"), 1),
                Arguments.of(new Filter(UserSearchKey.email.getName(), RelationalOperator.LIKE, "email2@gmail.com"), 1),
                Arguments.of(new Filter(UserSearchKey.expectedCaloriesPerDay.getName(), RelationalOperator.GREATER_THAN, 50), 1),
                Arguments.of(new Filter(UserSearchKey.expectedCaloriesPerDay.getName(), RelationalOperator.GREATER_THAN_EQUAL_TO, 50), 2),
                Arguments.of(new Filter(UserSearchKey.expectedCaloriesPerDay.getName(), RelationalOperator.LESS_THAN, 50), 0),
                Arguments.of(new Filter(UserSearchKey.expectedCaloriesPerDay.getName(), RelationalOperator.LESS_THAN_EQUAL_TO, 50), 1),
                Arguments.of(new Filter(UserSearchKey.expectedCaloriesPerDay.getName(), RelationalOperator.EQUAL, 100), 1),
                Arguments.of(new Filter(UserSearchKey.expectedCaloriesPerDay.getName(), RelationalOperator.NOT_EQUAL, 500), 2)
        );
    }

    @Test
    public void queryInvalidQueryException(){
        try {
            SpecificationBuilder<UserDTO> specBuilder = new SpecificationBuilder<UserDTO>();
            specBuilder.with(UNSUPPPORTED_KEY, RelationalOperator.EQUAL.getName(), true);
            Specification<UserDTO> spec = specBuilder.build(UserDTOSpecification::new);
            this.userRepository.findAll(spec);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof InvalidSearchQueryException);
        }


    }
}