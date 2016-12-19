package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class UserController {

    private static final String API_CONTEXT = "/api/v1";
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
    * The controller to setup the endpoints for the user portion of the API.
    * @param userService the service object to handle the endpoint.
    */
    public UserController(UserService userService) {
        this.userService = userService;
        setupEndpoints();
    }

    /**
    * Sets up the endpoints for a user.
    */
    private void setupEndpoints() {
        // create a new user
        post(API_CONTEXT + "/user", "application/json", (request, response) -> {
            try {
                User u = userService.createNewUser(request.body());
                response.status(201);
                return u;
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to create new user");
        if(ex instanceof UserService.NewUserException) {
            response.status(411);
        } else {
            response.status(410);
        }
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // get a new recommendation
        get(API_CONTEXT + "/recommendation/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);		
                return userService.getRecommendation(Integer.parseInt(request.params(":userId")));
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to generate recommendation");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
    }
}
