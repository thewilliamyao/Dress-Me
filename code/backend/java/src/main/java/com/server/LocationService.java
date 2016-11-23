package com.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.github.dvdme.ForecastIOLib.*;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class LocationService {

    private static Sql2o db;
    private static int locationCounter = 0; // counter for free value of the id
    private static final double BALTIMORE_LATITUDE = 39.330496; // used for default location
    private static final double BALTIMORE_LONGITUDE = -76.620046; // used for default location
    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    /**
    * Construct the model with a pre-defined datasource. The current implementation
    * also ensures that the DB schema is created if necessary.
    *
    * @param currDb the sq2o object containing the connection to the database.
    */
    public LocationService(Sql2o currDb) throws LocationServiceException {
        db = currDb;
        
        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sqlLocation = "CREATE TABLE IF NOT EXISTS locations (location_id INTEGER PRIMARY KEY, " +
                         "                                  user_id INTEGER, latitude DECIMAL, longitude DECIMAL)";
            conn.createQuery(sqlLocation).executeUpdate();

            String sqlLocationId = "SELECT MAX(location_id) FROM locations";
            Integer latestLocation = conn.createQuery(sqlLocationId)
                .addColumnMapping("location_id", "locationId")
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("latitute", "latitute")
                .addColumnMapping("longitude", "longitude")
                .executeAndFetchFirst(Integer.class);
            if (latestLocation != null) {
                locationCounter = latestLocation.intValue() + 1;
            } else {
                locationCounter = 0;
            }
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new LocationServiceException("Failed to create schema at startup", ex);
        }
    }
    
    /**
    * Creates a new location for a user. Updates the db. Defaults to Baltimore.
    * @param userId, the id of the user.
    */
    public static void createNewLocation(int userId) throws LocationServiceException {
        int currLocationId = locationCounter++;
        // give default location of baltimore
        double longitude = BALTIMORE_LONGITUDE;
        double latitude = BALTIMORE_LATITUDE;
        String sqlLocation = "INSERT INTO locations (location_id, user_id, latitude, longitude)" +
                "                  VALUES (:locationId, :userId, :latitude, :longitude)";
        try (Connection conn = db.open()) {
            conn.createQuery(sqlLocation)
                .addColumnMapping("location_id", "locationId")
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("latitude", "latitude")
                .addColumnMapping("longitude", "longitude")
                .addParameter("userId", userId)
                .addParameter("locationId", currLocationId)
                .addParameter("longitude", longitude)
                .addParameter("latitude", latitude)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("LocationService.createNewLocation: Failed to add new user entry", ex);
            throw new LocationServiceException("LocationService.createNewLocation: Failed to add new user entry", ex);
        }
    }

    /**
    * Gets the location object of a user.
    * @param userId, the id of the user.
    * @return the location object of the user.
    */
    public static Location getLocation(int userId) throws LocationServiceException {
        String sqlLocation = "SELECT * FROM locations WHERE user_id = :userId";
        try (Connection conn = db.open()) {
            Location currLocation = 
                conn.createQuery(sqlLocation)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("location_id", "locationId")
                    .addColumnMapping("latitude", "latitude")
                    .addColumnMapping("longitude", "longitude")
                    .addParameter("userId", userId)
                    .executeAndFetchFirst(Location.class);
            return currLocation;
        } catch (Sql2oException ex) {
                logger.error("LocationService.getLocation: Failed to get location", ex);
            throw new LocationServiceException("LocationService.getLocation: Failed to get location", ex);
        }
    }

    /**
    * Updates the location of a user.
    * @param id the id of a user.
    * @body the json form of the update, {latitude: x, longitude: y}.
    * @return the new location of the user.
    */
    public Location updateLocation(String id, String body) throws LocationServiceException {
        // grab params
        int currId = Integer.parseInt(id);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        double latitude = obj.get("latitude").getAsDouble();
        double longitude = obj.get("longitude").getAsDouble();

        // update location in table
        String sqlUpdateLocation = "UPDATE locations SET latitude = :latitude, longitude = :longitude WHERE user_id = :userId";
        try (Connection conn = db.open()) {
            conn.createQuery(sqlUpdateLocation)
                .addColumnMapping("location_id", "locationId")
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("latitude", "latitude")
                .addColumnMapping("longitude", "longitude")
                .addParameter("userId", currId)
                .addParameter("latitude", latitude)
                .addParameter("longitude", longitude)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("LocationService.updateLocation: Failed to update location", ex);
            throw new LocationServiceException("LocationService.updateLocation: Failed to update location", ex);
        }

        return getLocation(currId);
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class LocationServiceException extends Exception {
        public LocationServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class NewUserException extends LocationServiceException {
        public NewUserException(String message) {
            super(message, null);
        }
    }
}
