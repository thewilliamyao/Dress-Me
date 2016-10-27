package com.serverapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class UserController {

    private static final String API_CONTEXT = "/api/v1";

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        // create a new user
        post(API_CONTEXT + "/user", "application/json", (request, response) -> {
            try {
                User u = userService.createNewUser(request.body());
                response.status(201);
                return u;
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to create new user");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // get a user's closet
        get(API_CONTEXT + "/user/closet/:userid", "application/json", (request, response) -> {
            try {
                return userService.getClothesMap(Integer.parseInt(request.params(":userId")));
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to create new user");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // update a user's clothes counts
        put(API_CONTEXT + "/user/closet/:userId", "application/json", (request, response) -> {
            try {
                return userService.updateClothes(request.params(":userId"), request.body());
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to update closet");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
        // get a new recommendation
        get(API_CONTEXT + "/recommendation/:userid/:recommendationNum", "application/json", (request, response) -> {
            try {
                return userService.getRecommendation(Integer.parseInt(request.params(":userId")), Integer.parseInt(request.params(":recommendationNum")));
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to create new user");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
    }
}
