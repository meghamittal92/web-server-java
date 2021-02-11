package com.client.calorieserver.accessor;

import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.repository.CaloriePerDayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class NutritionixCalorieAccessorImplTest {

    private NutritionixCalorieAccessorImpl accessor;
    private final String apiKey = "api_key";
    private final String appId = "app_id";

    @Mock
    Client client;

    @Mock
    WebTarget target;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        accessor = new NutritionixCalorieAccessorImpl(apiKey, appId);
    }

    @Test
    void getCalories(){

    }
}
