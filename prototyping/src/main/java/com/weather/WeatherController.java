//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.weatherapp;

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
        /*
        post(API_CONTEXT + "/todos", "application/json", (request, response) -> {
            try {
                todoService.createNewTodo(request.body());
                response.status(201);
            } catch (WeatherService.TodoServiceException ex) {
                logger.error("Failed to create new entry");
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        get(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                return todoService.find(request.params(":id"));
            } catch (WeatherService.TodoServiceException ex) {
                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/todos", "application/json", (request, response)-> {
            try {
                return todoService.findAll() ;
            } catch (TodoService.TodoServiceException ex) {
                logger.error("Failed to fetch the list of todos");
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        put(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                return todoService.update(request.params(":id"), request.body());
            } catch (TodoService.TodoServiceException ex) {
                logger.error(String.format("Failed to update todo with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        delete(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
            try {
                todoService.delete(request.params(":id"));
                response.status(200);
            } catch (TodoService.TodoServiceException ex) {
                logger.error(String.format("Failed to delete todo with id: %s", request.params(":id")));
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
*/
    }
}
