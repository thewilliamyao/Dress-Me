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
        // TODO some error checking on invalid username (i.e already in use)
        post(API_CONTEXT + "/user", "application/json", (request, response) -> {
            try {
                /*
                String token = request.headers("TOKEN");
                if (!token.equals("TSAFDOJSDFJ")) {
                    response.status(411);
                    return Collections.EMPTY_MAP;
                }*/
                LoginToken l = userService.createNewUser(request.body());
                response.status(201);
                return l;
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

        //logs in for a user
        get(API_CONTEXT + "/login", "application/json", (request, response) -> {
            try {
                response.status(200);
                return userService.getLoginToken(request.body());
            } catch (UserService.UserServiceException ex) {
                logger.error("Invalid credentials");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // get a new recommendation
        get(API_CONTEXT + "/recommendation/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(401);
                    return Collections.EMPTY_MAP;
                }
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
