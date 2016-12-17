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
        get(API_CONTEXT + "/reset", "application/json", (request, response) -> {
            userService.reset();
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // create a new user
        post(API_CONTEXT + "/user", "application/json", (request, response) -> {
            response.status(201);
            LoginToken l = userService.createNewUser(request.body());
            if (l.getId() == -1) {
                response.status(403);
            } else if (l.getId() == -2) {
                response.status(420);
            }
            return l;
        }, new JsonTransformer());

        //logs in for a user
        put(API_CONTEXT + "/login", "application/json", (request, response) -> {
            response.status(200);
            LoginToken l = userService.getLoginToken(request.body());
            if (l.getId() == -1) {
                response.status(403);
            } else if (l.getId() == -2) {
                response.status(420);
            }
            return l;
        }, new JsonTransformer());

        // get a new recommendation
        get(API_CONTEXT + "/recommendation/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(403);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                return userService.getRecommendation(Integer.parseInt(request.params(":userId")));
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to generate recommendation");
                response.status(420);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
    }
}
