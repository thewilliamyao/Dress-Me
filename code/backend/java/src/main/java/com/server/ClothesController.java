package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class ClothesController {

    private static final String API_CONTEXT = "/api/v1";
    private final ClothesService clothesService;
    private final Logger logger = LoggerFactory.getLogger(ClothesController.class);

    /**
    * The controller to setup the endpoints for the clothes portion of the API.
    * @param clothesService the service object to handle the endpoint.
    */
    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
        setupEndpoints();
    }

    /**
    * Sets up the endpoints for clothes.
    */
    private void setupEndpoints() {
        // get a user's closet
        get(API_CONTEXT + "/closet/:userId", "application/json", (request, response) -> {
            try {                
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(403);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                return clothesService.getClothesMap(currId);
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to create new user");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // get a user's laundry
        get(API_CONTEXT + "/laundry/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(403);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                return clothesService.getLaundryMap(Integer.parseInt(request.params(":userId")));
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to create new user");
                response.status(420);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // update a user's clothes counts
        put(API_CONTEXT + "/closet/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(403);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                return clothesService.updateClothes(request.params(":userId"), request.body());
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to update closet");
                response.status(420);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // update a user's laundry counts
        put(API_CONTEXT + "/laundry/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(403);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                return clothesService.updateLaundry(request.params(":userId"), request.body());
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to update closet");
                response.status(420);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        // mark items as choosen (which in turn makes them dirty)
        put(API_CONTEXT + "/dirty/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(403);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                return clothesService.markDirty(Integer.parseInt(request.params("userId")), request.body());
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to mark items as dirty", ex);
                response.status(420);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());


        // reset laundry, set all dirty counts to 0
        put(API_CONTEXT + "/clean/:userId", "application/json", (request, response) -> {
            try {
                int currId = Integer.parseInt(request.params(":userId"));
                if (!LoginToken.verify(request.headers("token"), currId)) {
                    response.status(403);
                    return Collections.EMPTY_MAP;
                }
                response.status(200);
                clothesService.markClean(Integer.parseInt(request.params("userId")));
            } catch (ClothesService.ClothesServiceException ex) {
                logger.error("Failed to mark items as clean", ex);
                response.status(420);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());
    }
}
