package com.client.calorieserver.accessor;

import com.client.calorieserver.domain.exception.ExternalCalorieServiceException;
import com.client.calorieserver.domain.model.calorie.accessor.NutritionixErrorResponse;
import com.client.calorieserver.domain.model.calorie.accessor.NutritionixRequest;
import com.client.calorieserver.domain.model.calorie.accessor.NutritionixResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class NutritionixCalorieAccessorImpl implements CalorieAccessor {

    private final String apikey;

    private final String appId;

    @Autowired
    public NutritionixCalorieAccessorImpl(String apikey, String appId) {
        this.apikey = apikey;
        this.appId = appId;
        client = ClientBuilder.newClient();
        target = client.target(
                NUTRITIONIX_ENDPOINT_URI);
    }

    private static final String NUTRITIONIX_ENDPOINT_URI = "https://trackapi.nutritionix.com/";
    private static final String NUTRITIONIX_GET_CALORIES_PATH = "v2/natural/nutrients";
    private static final String API_KEY_HEADER_NAME = "x-app-key";
    private static final String APP_ID_HEADER_NAME = "x-app-id";
    private static final String REMOTE_USER_ID_HEADER_NAME = "x-remote-user-id";


    private final Client client;
    private final WebTarget target;


    @Override
    public Integer getCalories(final String mealDetails, final Long userID) {
        final NutritionixRequest nutritionixRequest = new NutritionixRequest(mealDetails);
        final Entity entity = Entity.json(nutritionixRequest);

        final Response response = target.path(NUTRITIONIX_GET_CALORIES_PATH)
                .request(MediaType.APPLICATION_JSON)
                .header(API_KEY_HEADER_NAME, apikey)
                .header(APP_ID_HEADER_NAME, appId)
                .header(REMOTE_USER_ID_HEADER_NAME, userID)
                .post(entity);

        final int responseStatus = response.getStatus();


        if (responseStatus == Response.Status.OK.getStatusCode() && response.getEntity() != null) {
            NutritionixResponse nutritionixResponse = response.readEntity(NutritionixResponse.class);
            return nutritionixResponse.getCalories();

        } else {
            NutritionixErrorResponse nutritionixErrorResponse = response.readEntity(NutritionixErrorResponse.class);
            throw new ExternalCalorieServiceException(nutritionixErrorResponse.getMessage());
        }


    }
}
