package com.serverapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class WeatherController {

    private static final String API_CONTEXT = "/api/v1";

    private final WeatherService weatherService;

    private final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    public WeatherController(WeatherService weather) {
        this.weatherService = weather;
        setupEndpoints();
    }

    private void setupEndpoints() {
        get(API_CONTEXT + "/weathers", "application/json", (request, response) -> {
            try {
                return weatherService.test();
            } catch (WeatherService.WeatherServiceException ex) {
                logger.error("Failed to get new entry");
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
    }
}
