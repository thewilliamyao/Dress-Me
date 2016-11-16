package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class ClothesController {

    private static final String API_CONTEXT = "/api/v1";

    private final ClothesService clothesService;

    private final Logger logger = LoggerFactory.getLogger(ClothesController.class);

    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        // get a user's closet
        get(API_CONTEXT + "/closet/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
                return clothesService.getClothesMap(Integer.parseInt(request.params(":userId")));
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to create new user");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // update a user's clothes counts
        put(API_CONTEXT + "/closet/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
                return clothesService.updateClothes(request.params(":userId"), request.body());
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to update closet");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // mark items as choosen (which in turn makes them dirty)
        put(API_CONTEXT + "/dirty/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
                return clothesService.markDirty(Integer.parseInt(request.params("userId")), request.body());
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to generate recommendation");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());


        // reset laundry, set all dirty counts to 0
        put(API_CONTEXT + "/clean/:userId", "application/json", (request, response) -> {
            try {
                response.status(200);
                clothesService.markClean(Integer.parseInt(request.params("userId")));
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to generate recommendation");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());    }
}
