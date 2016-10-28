package com.serverapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import com.github.dvdme.ForecastIOLib.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.HashMap;

public class UserService {

    private Sql2o db;
    private static int userCounter;
    private static int clothesCounter;
    private static int locationCounter;
    private String[] allTops = {"tank_top", "t_shirt", "long_sleeve"};
    private String[] allPants = {"shorts", "long_pants"};
    private String[] allOuterwear = {"hoodie", "windbreaker", "sweater"};
    private String[] allFootwear = {"shoes", "boots", "sandals"};
    private String[] allAccessories = {"umbrella", "scarf"};
    private HashMap<String, Double> lowTempMap;
    private HashMap<String, Double> highTempMap;
    private double TEMP_MAX = 120.0;
    private double TEMP_MIN = -120.0;
    private final double BALTIMORE_LATITUDE = 39.330496;
    private final double BALTIMORE_LONGITUDE = -76.620046;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    
    public UserService(DataSource dataSource) throws UserServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        // TODO database stuff
        try (Connection conn = db.open()) {
            String sqlUser = "CREATE TABLE IF NOT EXISTS users (user_id INTEGER PRIMARY KEY, " +
                         "                                 email TEXT, password TEXT)" ;
            conn.createQuery(sqlUser).executeUpdate();

            String sqlClothes = "CREATE TABLE IF NOT EXISTS clothes (clothes_id INTEGER PRIMARY KEY, " +
                         "                                 user_id INTEGER, type TEXT, specific_type TEXT, number_owned INTEGER, " + 
                         "                                 number_dirty INTEGER, temp_high DOUBLE, temp_low DOUBLE)" ;
            conn.createQuery(sqlClothes).executeUpdate();

            String sqlLocation = "CREATE TABLE IF NOT EXISTS locations (location_id INTEGER PRIMARY KEY, " +
                         "                                  user_id INTEGER, latitude DOUBLE, longitude DOUBLE)";
            conn.createQuery(sqlLocation).executeUpdate();

            String sqlLocationId = "SELECT MAX(location_id) FROM locations";
            Integer latestLocation = conn.createQuery(sqlLocationId)
                .addColumnMapping("location_id", "locationId")
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("latitute", "latitute")
                .addColumnMapping("longitude", "longitude")
                .executeAndFetchFirst(Integer.class);
            if (latestLocation != null) {
                this.locationCounter = latestLocation.intValue() + 1;
            }

            String sqlUserId = "SELECT MAX(user_id) FROM users";

            Integer latestUser = conn.createQuery(sqlUserId)
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("email", "email")
                .addColumnMapping("password", "password")
                .executeAndFetchFirst(Integer.class);
            if (latestUser != null) {
                this.userCounter = latestUser.intValue() + 1;
            }

            String sqlClothesId = "SELECT MAX(clothes_id) FROM clothes";

            Integer latestClothes = conn.createQuery(sqlUserId)
                .addColumnMapping("clothes_id", "clothesId")
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("type", "type")
                .addColumnMapping("number_owned", "numberOwned")
                .addColumnMapping("number_dirty", "numberDirty")
                .addColumnMapping("temp_high", "tempHigh")
                .addColumnMapping("temp_low", "tempLow")
                .executeAndFetchFirst(Integer.class);
            if (latestClothes != null) {
                this.clothesCounter = latestClothes.intValue() + 1;
            }

            // create default mapping of temperatures
            // TODO: turn this into a static DB
            lowTempMap = new HashMap<String, Double>();
            highTempMap = new HashMap<String, Double>();
            lowTempMap.put("tank_top", 80.0);
            highTempMap.put("tank_top", TEMP_MAX);
            lowTempMap.put("t_shirt", TEMP_MIN);
            highTempMap.put("t_shirt", TEMP_MAX);
            lowTempMap.put("long_sleeve", TEMP_MIN);
            highTempMap.put("long_sleeve", 80.0);
            lowTempMap.put("shorts", 60.0);
            highTempMap.put("shorts", 120.0);
            lowTempMap.put("long_pants", TEMP_MIN);
            highTempMap.put("long_pants", 80.0);
            lowTempMap.put("hoodie", TEMP_MIN);
            highTempMap.put("hoodie", 120.0);

        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException("Failed to create schema at startup", ex);
        }
    }
    
    // TODO: address the fact that we dont have default ranges for some values...
    /// probably hardcode these in
    public void addClothing(String type, String specificType [], int currUserId) throws UserServiceException {
        String sqlClothes = "INSERT INTO clothes (clothes_id, user_id, type, specific_type, number_owned, number_dirty, temp_high, temp_low)" + 
                "                   VALUES (:clothesId, :userId, :type, :specificType, :numberOwned, :numberDirty, :tempHigh, :tempLow) ";

        for(String s : specificType) {
            try (Connection conn = db.open()) {
                conn.createQuery(sqlClothes)
                        .addColumnMapping("user_id", "userId")
                        .addColumnMapping("clothes_id", "clothesId")
                        .addColumnMapping("type", "type")
                        .addColumnMapping("specific_type", "specificType")
                        .addColumnMapping("number_owned", "numberOwned")
                        .addColumnMapping("number_dirty", "numberDirty")
                        .addColumnMapping("temp_high", "tempHigh")
                        .addColumnMapping("temp_low", "tempLow")
                        .addParameter("userId", currUserId)
                        .addParameter("clothesId", this.clothesCounter++)
                        .addParameter("type", "top")
                        .addParameter("specificType", s)
                        .addParameter("numberOwned", 0)
                        .addParameter("numberDirty", 0)
                        .addParameter("tempHigh", 0.0)
                        .addParameter("tempLow", 0.0)
                    .executeUpdate();
            } catch (Sql2oException ex) {
                logger.error("UserService.addClothing: Failed to add new clothing entry", ex);
                throw new UserServiceException("UserService.addClothing: Failed to add new clothing entry", ex);
            }
        }
    }

    public User createNewUser(String body) throws UserServiceException {
        User user = new Gson().fromJson(body, User.class);
        int currUserId = this.userCounter++;
        int currLocationId = this.locationCounter++;
        user.setUserId(currUserId);

        // TODO password encryption/token authentication
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String currEmail = obj.get("email").getAsString();
        String currPassword = obj.get("password").getAsString();
        // give default location of baltimore
        // TODO change this
        double longitude = BALTIMORE_LONGITUDE;
        double latitude = BALTIMORE_LATITUDE;

        // now populate default clothing
        addClothing("tops", allTops, currUserId);
        addClothing("pants", allPants, currUserId);
        addClothing("outerwear", allOuterwear, currUserId);
        addClothing("footwear", allFootwear, currUserId);
        addClothing("accessories", allAccessories, currUserId);

        // add user to database
        String sqlUser = "INSERT INTO users (user_id, email, password)" +
                "                  VALUES (:userId, :email, :password)";
        String sqlLocation = "INSERT INTO locations (location_id, user_id, latitude, longitude)" +
                "                  VALUES (:locationId, :userId, :latitude, :longitude)";
        try (Connection conn = db.open()) {
            conn.createQuery(sqlUser)
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("email", "email")
                .addColumnMapping("password", "password")
                .addParameter("userId", currUserId)
                .addParameter("email", currEmail)
                .addParameter("password", currPassword)
                .executeUpdate();

            conn.createQuery(sqlLocation)
                .addColumnMapping("location_id", "locationId")
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("latitude", "latitude")
                .addColumnMapping("longitude", "longitude")
                .addParameter("userId", currUserId)
                .addParameter("locationId", currLocationId)
                .addParameter("longitude", longitude)
                .addParameter("latitude", latitude)
                .executeUpdate();
            } catch (Sql2oException ex) {
                logger.error("UserService.createNewUser: Failed to add new user entry", ex);
                throw new UserServiceException("UserService.createNewUser: Failed to add new user entry", ex);
            }

    
        return user;
    }

    public HashMap<String, Integer> getClothesMap(int id) throws UserServiceException {
        String sqlClothes = "SELECT * FROM clothes WHERE user_id = :userId";

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        try (Connection conn = db.open()) {
            List<Clothes> allClothes = 
                conn.createQuery(sqlClothes)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("clothes_id", "clothesId")
                    .addColumnMapping("type", "type")
                    .addColumnMapping("specific_type", "specificType")
                    .addColumnMapping("number_owned", "numberOwned")
                    .addColumnMapping("number_dirty", "numberDirty")
                    .addColumnMapping("temp_high", "tempHigh")
                    .addColumnMapping("temp_low", "tempLow")
                    .addParameter("userId", id)
                    .executeAndFetch(Clothes.class);

            for (Clothes c : allClothes) {
                map.put(c.getSpecificType(), c.getNumberOwned());
            }

        } catch (Sql2oException ex) {
                logger.error("UserService.getClothesMap: Failed to get clothes map", ex);
                throw new UserServiceException("UserService.getClothesMap: Failed to get clothes map", ex);
        }
        return map;
    }

    public Location getLocation(int userId) throws UserServiceException {
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
                logger.error("UserService.getLocation: Failed to get location", ex);
                throw new UserServiceException("UserService.getLocation: Failed to get location", ex);
        }
    }

    public Location updateLocation(String id, String body) throws UserServiceException {
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
                logger.error("UserService.updateLocation: Failed to update location", ex);
                throw new UserServiceException("UserService.updateLocation: Failed to update location", ex);
        }

        return getLocation(currId);
    }

    public HashMap<String, Integer> updateClothes(String id, String body) throws UserServiceException {
        // grab params
        int currId = Integer.parseInt(id);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String clothesType = obj.get("type").getAsString();
        int number = obj.get("number").getAsInt();
        // update specific item
        String updateItem = "UPDATE clothes SET number_owned = :numberOwned WHERE (user_id = :userId AND specific_type = :specificType)";

        try (Connection conn = db.open()) {
            conn.createQuery(updateItem)
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("clothes_id", "clothesId")
                .addColumnMapping("type", "type")
                .addColumnMapping("specific_type", "specificType")
                .addColumnMapping("number_owned", "numberOwned")
                .addColumnMapping("number_dirty", "numberDirty")
                .addColumnMapping("temp_high", "tempHigh")
                .addColumnMapping("temp_low", "tempLow")
                .addParameter("specificType", clothesType)
                .addParameter("numberOwned", number)
                .addParameter("userId", currId)
                .executeUpdate();
        } catch (Sql2oException ex) {
                logger.error("UserService.updateClothes: Failed to update clothes", ex);
                throw new UserServiceException("UserService.updateClothes: Failed to update clothes", ex);
        }

        return getClothesMap(currId);
    }

    public Recommendation getRecommendation(int currId, int recommendationNum) throws UserServiceException {
        // generate three recommendations.
        // TODO Maybe store in database, so we don't have to constantly ping weather and re-calculate.
        // Weather currWeather = new Weather();
        // // set pants, purely off of weather
        // if (currWeather.getMaxTemp() > highTempMap()) {
        // }
/*
// determine type of pants
        -determine based on just temperature
    // determine types of footwear
        -precipType
            -if its raining or snowing
                boots
        -temperature 
            -if its cold, not sandals
    // determine accessories
        -if raining and not super windy
            -umbrella
        -if really cold or really windy
            -scarf
    // determine shirt
        -if its really hot (over 85)
            -tank top
        -if its cold
            -prefer a long sleeve
    // determine outerwear
        -if its temperature really cold or snowing
            -winter coat
        -if its super windy
            -windbreaker
        -if its raining
            -rain jacket
        -its its not raining and somewhat cold
            -hoodie
            -sweater

*/
        return new Recommendation("t_shirt", "long_pants", "shoes", "none", "hoodie");
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
