package com.server;

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
		if(ex instanceof UserService.NewUserException) {
		    response.status(411);
		} else {
		    response.status(410);
		}
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // get a user's closet
        get(API_CONTEXT + "/user/closet/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
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
                response.status(200);
                return userService.updateClothes(request.params(":userId"), request.body());
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to update closet");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // update a user's location
        put(API_CONTEXT + "/user/location/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
                return userService.updateLocation(request.params(":userId"), request.body());
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to update location");
                response.status(410);
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

        // mark items as choosen (which in turn makes them dirty)
        put(API_CONTEXT + "/user/dirty/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
                return userService.markDirty(Integer.parseInt(request.params("userId")), request.body());
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to generate recommendation");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());


        // reset laundry, set all dirty counts to 0
        put(API_CONTEXT + "/user/clean/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
                userService.markClean(Integer.parseInt(request.params("userId")));
            } catch (UserService.UserServiceException ex) {
                logger.error("Failed to generate recommendation");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());    }
}
