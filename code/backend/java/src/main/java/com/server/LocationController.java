package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class LocationController {

    private static final String API_CONTEXT = "/api/v1";
    private final LocationService locationService;
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    /**
    * The controller to setup the endpoints for the location portion of the API.
    * @param locationService the service object to handle the endpoint.
    */
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
        setupEndpoints();
    }

    /**
    * Sets up the endpoints for a location.
    */
    private void setupEndpoints() {
        // update a user's location
        put(API_CONTEXT + "/location/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(401);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                return locationService.updateLocation(request.params(":userId"), request.body());
            } catch (LocationService.LocationServiceException ex) {
                logger.error("Failed to update location");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
    }
}
