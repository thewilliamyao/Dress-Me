package com.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.github.dvdme.ForecastIOLib.*;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;

public class ClothesService {
    private static Sql2o db;
    private static int clothesCounter = 0; // counter for free value of the id
    private static String[] allTops = {"tank_top", "t_shirt", "long_sleeve"}; // types of clothing
    private static String[] allPants = {"shorts", "long_pants"}; // types of clothing
    private static String[] allOuterwear = {"hoodie", "windbreaker", "sweater", "winter_coat", "rain_jacket"}; // types of clothing
    private static String[] allFootwear = {"shoes", "boots", "sandals"}; // types of clothing
    private static String[] allAccessories = {"umbrella", "scarf"}; // types of clothing
    private static HashMap<String, Double> lowTempMap; // map to hold defaults for high and low temps
    private static HashMap<String, Double> highTempMap; // map to hold defaults for high and low temps
    private static HashMap<String, Integer> timesWornMap; // map to hold max number of times each item can be worn
    private static final double TEMP_MAX = 120.0; // limit for temp values
    private static final double TEMP_MIN = -120.0; // limit for temp values
    private static final Logger logger = LoggerFactory.getLogger(ClothesService.class);

    /**
    * Construct the model with a pre-defined datasource. The current implementation
    * also ensures that the DB schema is created if necessary.
    *
    * @param currDb the sq2o object containing the connection to the database.
    */
    public ClothesService (Sql2o currDb) throws ClothesServiceException {
        db = currDb;
        try (Connection conn = db.open()) {
            String sqlClothes = "CREATE TABLE IF NOT EXISTS clothes (clothes_id INTEGER PRIMARY KEY, " +
                         "                                 user_id INTEGER, type TEXT, specific_type TEXT, number_owned INTEGER, " + 
                         "                                 number_dirty INTEGER, temp_high DECIMAL, temp_low DECIMAL, times_worn INTEGER, max_times_worn INTEGER)" ;
            conn.createQuery(sqlClothes).executeUpdate();
            String sqlClothesId = "SELECT MAX(clothes_id) FROM clothes";
            Integer latestClothes = conn.createQuery(sqlClothesId)
                .addColumnMapping("clothes_id", "clothesId")
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("type", "type")
                .addColumnMapping("number_owned", "numberOwned")
                .addColumnMapping("number_dirty", "numberDirty")
                .addColumnMapping("temp_high", "tempHigh")
                .addColumnMapping("temp_low", "tempLow")
                .addColumnMapping("times_worn", "timesWorn")
                .addColumnMapping("max_times_worn", "maxTimesWorn")
                .executeAndFetchFirst(Integer.class);
            if (latestClothes != null) {
                clothesCounter = latestClothes.intValue() + 1;
            } else {
                clothesCounter = 0;
            }
            // create default mapping of temperatures
            lowTempMap = new HashMap<String, Double>();
            highTempMap = new HashMap<String, Double>();
            timesWornMap = new HashMap<String, Integer>();
            // shirts
            lowTempMap.put("tank_top", new Double(80));
            highTempMap.put("tank_top", new Double(TEMP_MAX));
            timesWornMap.put("tank_top", new Integer(1));
            lowTempMap.put("t_shirt", new Double(TEMP_MIN));
            highTempMap.put("t_shirt", new Double(TEMP_MAX));
            timesWornMap.put("t_shirt", new Integer(1));
            lowTempMap.put("long_sleeve", new Double(TEMP_MIN));
            highTempMap.put("long_sleeve", new Double(80));
            timesWornMap.put("long_sleeve", new Integer(1));
            // pants
            lowTempMap.put("shorts", new Double(60));
            highTempMap.put("shorts", new Double(120));
            timesWornMap.put("shorts", new Integer(3));
            lowTempMap.put("long_pants", new Double(TEMP_MIN));
            highTempMap.put("long_pants", new Double(80));
            timesWornMap.put("long_pants", new Integer(3));
            // outerwear
            lowTempMap.put("hoodie", new Double(50));
            highTempMap.put("hoodie", new Double(80));
            timesWornMap.put("hoodie", new Integer(-1));
            lowTempMap.put("windbreaker", new Double(50));
            highTempMap.put("windbreaker", new Double(TEMP_MAX));
            timesWornMap.put("windbreaker", new Integer(-1));
            lowTempMap.put("sweater", new Double(50));
            highTempMap.put("sweater", new Double(80));
            timesWornMap.put("sweater", new Integer(-1));
            lowTempMap.put("winter_coat", new Double(TEMP_MIN));
            highTempMap.put("winter_coat", new Double(40));
            timesWornMap.put("winter_coat", new Integer(-1));
            // accessories
            lowTempMap.put("umbrella", new Double(TEMP_MIN));
            highTempMap.put("umbrella", new Double(TEMP_MAX));
            timesWornMap.put("umbrella", new Integer(-1));
            lowTempMap.put("scarf", new Double(TEMP_MIN));
            highTempMap.put("scarf", new Double(50));
            timesWornMap.put("scarf", new Integer(-1));
            // shoes
            lowTempMap.put("boots", new Double(TEMP_MIN));
            highTempMap.put("boots", new Double(40));
            timesWornMap.put("boots", new Integer(-1));
            lowTempMap.put("sandals", new Double(80));
            highTempMap.put("sandals", new Double(TEMP_MAX));
            timesWornMap.put("sandals", new Integer(-1));
            lowTempMap.put("shoes", new Double(TEMP_MIN));
            highTempMap.put("shoes", new Double(TEMP_MAX));
            timesWornMap.put("shoes", new Integer(-1));
        }
    }

    /**
    * Adds default values to the clothes db for a user.
    * @param currUserId the id of the user.
    */
    public static void createNewClothes(int currUserId) throws ClothesServiceException {
        // now populate default clothing
        addClothing("top", allTops, currUserId);
        addClothing("pants", allPants, currUserId);
        addClothing("outerwear", allOuterwear, currUserId);
        addClothing("footwear", allFootwear, currUserId);
        addClothing("accessory", allAccessories, currUserId);
    }

    /**
    * Adds a specific type of default clothing values for a user into the
    * backend db.
    * @param type the type of clothing item [top, pants, outerwear, footwear, accesory].
    * @param specificType an array of the specific types of clothing.
    * @param currUserId the id corresponding to the user.
    */
    public static void addClothing(String type, String specificType [], int currUserId) throws ClothesServiceException {
        String sqlClothes = "INSERT INTO clothes (clothes_id, user_id, type, specific_type, number_owned, number_dirty, temp_high, temp_low, times_worn, max_times_worn)" + 
                "                   VALUES (:clothesId, :userId, :type, :specificType, :numberOwned, :numberDirty, :tempHigh, :tempLow, :timesWorn, :maxTimesWorn) ";

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
                        .addColumnMapping("times_worn", "timesWorn")
                        .addColumnMapping("max_times_worn", "maxTimesWorn")
                        .addParameter("userId", currUserId)
                        .addParameter("clothesId", clothesCounter++)
                        .addParameter("type", type)
                        .addParameter("specificType", s)
                        .addParameter("numberOwned", 0) //TODO change later to 0
                        .addParameter("numberDirty", 0)
                        .addParameter("tempHigh", highTempMap.containsKey(s) ? highTempMap.get(s) : TEMP_MAX)
                        .addParameter("tempLow", lowTempMap.containsKey(s) ? lowTempMap.get(s) : TEMP_MIN)
                        .addParameter("timesWorn", 0)
                        .addParameter("maxTimesWorn", timesWornMap.containsKey(s) ? timesWornMap.get(s).intValue() : -1)
                    .executeUpdate();
            } catch (Sql2oException ex) {
                logger.error("ClothesService.addClothing: Failed to add new clothing entry", ex);
                throw new ClothesServiceException("ClothesService.addClothing: Failed to add new clothing entry", ex);
            }
        }
    }

    /**
    * Returns the list of clothes items from a specific user.
    * @param id the id of the user.
    * @return the list of clothes items.
    */
    public static List<Clothes> getClothesList(int id) throws ClothesServiceException {
        String sqlClothes = "SELECT * FROM clothes WHERE user_id = :userId";
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
                    .addColumnMapping("times_worn", "timesWorn")
                    .addColumnMapping("max_times_worn", "maxTimesWorn")
                    .addParameter("userId", id)
                    .executeAndFetch(Clothes.class);
            return allClothes;
        } catch (Sql2oException ex) {
            logger.error("ClothesService.getClothesList: Failed to get clothes map", ex);
            throw new ClothesServiceException("ClothesService.getClothesList: Failed to get clothes map", ex);
        }
    }

    /**
    * Returns a map of clothes items to the number of each item owned.
    * @param id the id of the user.
    * @return a map of the clothes item specific types to the number owned.
    */
    public static HashMap<String, Integer> getClothesMap(int id) throws ClothesServiceException {
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
                    .addColumnMapping("times_worn", "timesWorn")
                    .addColumnMapping("max_times_worn", "maxTimesWorn")
                    .addParameter("userId", id)
                    .executeAndFetch(Clothes.class);

            for (Clothes c : allClothes) {
                map.put(c.getSpecificType(), c.getNumberOwned());
            }

        } catch (Sql2oException ex) {
            logger.error("ClothesService.getClothesMap: Failed to get clothes map", ex);
            throw new ClothesServiceException("ClothesService.getClothesMap: Failed to get clothes map", ex);
        }
        return map;
    }

    /**
    * Returns a map of clothes items to the number of items that are dirty.
    * @param id the id of the user.
    * @return a map of the clothes item specific types to the number dirty.
    */
    public static HashMap<String, Integer> getLaundryMap(int id) throws ClothesServiceException {
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
                    .addColumnMapping("times_worn", "timesWorn")
                    .addColumnMapping("max_times_worn", "maxTimesWorn")
                    .addParameter("userId", id)
                    .executeAndFetch(Clothes.class);

            for (Clothes c : allClothes) {
                map.put(c.getSpecificType(), c.getNumberDirty());
            }

        } catch (Sql2oException ex) {
            logger.error("ClothesService.getLaundryMap: Failed to get laundry map", ex);
            throw new ClothesServiceException("ClothesService.getLaundryMap: Failed to get laundry map", ex);
        }
        return map;
    }

    /**
    * Updates the number of items owned for a specific user.
    * @param id the id of the user.
    * @param body the json form containing all clothes and their counts, {type: x, number: y}.
    * @return a map of the new counts for all items.
    */
    public HashMap<String, Integer> updateCloset(String id, String body) throws ClothesServiceException, InvalidInputException {
        // grab params
        int currId = Integer.parseInt(id);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String type = obj.get("type").getAsString();
        int number = obj.get("number").getAsInt();
        if (number < 0) { 
            throw new InvalidInputException("ClothesService.updateCloset: Negative input");
        }
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
                .addColumnMapping("times_worn", "timesWorn")
                .addColumnMapping("max_times_worn", "maxTimesWorn")
                .addParameter("specificType", type)
                .addParameter("numberOwned", number)
                .addParameter("userId", currId)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("ClothesService.updateCloset: Failed to update clothes", ex);
            throw new ClothesServiceException("ClothesService.updateCloset: Failed to update clothes", ex);
        }
        Clothes c = fetchClothes(currId, type);
        if (c != null) {
            // check to see if we need to update number dirty
            if (c.getNumberDirty() > c.getNumberOwned()) {
                try (Connection conn = db.open()) {
                    String updateDirty = "UPDATE clothes SET number_dirty = :numberDirty  WHERE (user_id = :userId AND specific_type = :specificType)";
                    conn.createQuery(updateDirty)
                        .addColumnMapping("user_id", "userId")
                        .addColumnMapping("clothes_id", "clothesId")
                        .addColumnMapping("type", "type")
                        .addColumnMapping("specific_type", "specificType")
                        .addColumnMapping("number_owned", "numberOwned")
                        .addColumnMapping("number_dirty", "numberDirty")
                        .addColumnMapping("temp_high", "tempHigh")
                        .addColumnMapping("temp_low", "tempLow")
                        .addColumnMapping("times_worn", "timesWorn")
                        .addColumnMapping("max_times_worn", "maxTimesWorn")
                        .addParameter("specificType", type)
                        .addParameter("numberDirty", c.getNumberOwned())
                        .addParameter("userId", currId)
                        .executeUpdate();
                } catch (Sql2oException ex) {
                    logger.error("ClothesService.updateCloset: Failed to update clothes", ex);
                    throw new ClothesServiceException("ClothesService.updateCloset: Failed to update clothes", ex);
                }
            }
        }
        /*
        Set<Map.Entry<String, JsonElement>> s = obj.entrySet();
        // update all items
        String updateItem = "UPDATE clothes SET number_owned = :numberOwned WHERE (user_id = :userId AND specific_type = :specificType)";
        try (Connection conn = db.open()) {
            for (Map.Entry<String, JsonElement> item : s) {
                conn.createQuery(updateItem)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("clothes_id", "clothesId")
                    .addColumnMapping("type", "type")
                    .addColumnMapping("specific_type", "specificType")
                    .addColumnMapping("number_owned", "numberOwned")
                    .addColumnMapping("number_dirty", "numberDirty")
                    .addColumnMapping("temp_high", "tempHigh")
                    .addColumnMapping("temp_low", "tempLow")
                    .addColumnMapping("times_worn", "timesWorn")
                    .addColumnMapping("max_times_worn", "maxTimesWorn")
                    .addParameter("specificType", item.getKey())
                    .addParameter("numberOwned", item.getValue().getAsInt())
                    .addParameter("userId", currId)
                    .executeUpdate();
            }
        } catch (Sql2oException ex) {
            logger.error("ClothesService.updateCloset: Failed to update clothes", ex);
            throw new ClothesServiceException("ClothesService.updateCloset: Failed to update clothes", ex);
        }
        */
        return getClothesMap(currId);
    }


    public Clothes fetchClothes(int id, String specificType) throws ClothesServiceException {
        try (Connection conn = db.open()) {
            String findItem = "SELECT * FROM clothes WHERE (user_id = :userId AND specific_type = :specificType)";
            return conn.createQuery(findItem)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("clothes_id", "clothesId")
                    .addColumnMapping("type", "type")
                    .addColumnMapping("specific_type", "specificType")
                    .addColumnMapping("number_owned", "numberOwned")
                    .addColumnMapping("number_dirty", "numberDirty")
                    .addColumnMapping("temp_high", "tempHigh")
                    .addColumnMapping("temp_low", "tempLow")
                    .addColumnMapping("times_worn", "timesWorn")
                    .addColumnMapping("max_times_worn", "maxTimesWorn")
                    .addParameter("userId", id)
                    .addParameter("specificType", specificType)
                    .executeAndFetchFirst(Clothes.class);
        } catch (Sql2oException ex) {
            logger.error("ClothesService.fetchClothes: Failed to fetch clothes", ex);
            throw new ClothesServiceException("ClothesService.fetchClothes: Failed to fetch clothes", ex);
        }
    }

    /**
    * Updates the number of items dirty for a specific user.
    * @param id the id of the user.
    * @param body the json form containing all clothes and their updates.
    * @return a map of the new counts of dirty items for all items.
    */
    public HashMap<String, Integer> updateLaundry(String id, String body) throws ClothesServiceException, InvalidInputException {
        // grab params
        int currId = Integer.parseInt(id);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String type = obj.get("type").getAsString();
        int number = obj.get("number").getAsInt();
        if (number < 0) {
            throw new InvalidInputException("ClothesService.updateLaundry: Negative input");
        } else {
            // check if its more than you own
            Clothes c = fetchClothes(currId, type);
            if (c == null) {
                throw new ClothesServiceException("ClothesService.updateLaundry: Could not find clothes item");
            }
            if (number > c.getNumberOwned()) {
                throw new InvalidInputException("ClothesService.updateLaundry: input more than number owned");
            }
        }
        String updateItem = "UPDATE clothes SET number_dirty = :numberDirty WHERE (user_id = :userId AND specific_type = :specificType)";
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
                .addColumnMapping("times_worn", "timesWorn")
                .addColumnMapping("max_times_worn", "maxTimesWorn")
                .addParameter("specificType", type)
                .addParameter("numberDirty", number)
                .addParameter("userId", currId)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("ClothesService.updateLaundry: Failed to update clothes", ex);
            throw new ClothesServiceException("ClothesService.updateLaundry: Failed to update clothes", ex);
        }
        /*
        Set<Map.Entry<String, JsonElement>> s = obj.entrySet();
        // update specific item
        String updateItem = "UPDATE clothes SET number_dirty = :numberDirty WHERE (user_id = :userId AND specific_type = :specificType)";

        try (Connection conn = db.open()) {
            for (Map.Entry<String, JsonElement> item : s) {
                conn.createQuery(updateItem)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("clothes_id", "clothesId")
                    .addColumnMapping("type", "type")
                    .addColumnMapping("specific_type", "specificType")
                    .addColumnMapping("number_owned", "numberOwned")
                    .addColumnMapping("number_dirty", "numberDirty")
                    .addColumnMapping("temp_high", "tempHigh")
                    .addColumnMapping("temp_low", "tempLow")
                    .addColumnMapping("times_worn", "timesWorn")
                    .addColumnMapping("max_times_worn", "maxTimesWorn")
                    .addParameter("specificType", item.getKey())
                    .addParameter("numberDirty", item.getValue().getAsInt())
                    .addParameter("userId", currId)
                    .executeUpdate();
            }
        } catch (Sql2oException ex) {
            logger.error("ClothesService.updateCloset: Failed to update clothes", ex);
            throw new ClothesServiceException("ClothesService.updateCloset: Failed to update clothes", ex);
        }
        */
        return getLaundryMap(currId);
    }
    /**
    * Helper function to evaluate if an item should be marked dirty. Does the appropriate updates as necessary.
    * Also updates the db.
    * @param dirtyMap a reference to a hashmap with the number of dirty items
    * @param timesWornMap a reference to a hashmap with the number of times an item was worn
    * @param maxTimesWornMap a reference to a hashmap with the number of tiems an item can be worn
    */
    private void evaluateDirty(int currId, HashMap<String, Integer> dirtyMap, HashMap<String, Integer> timesWornMap, HashMap<String, Integer> maxTimesWornMap, HashMap<String, Integer> numberOwnedMap, String item) throws ClothesServiceException {
        // if the key is not present or the max times worn is -1, we can skip this item.
        if (!maxTimesWornMap.containsKey(item) || maxTimesWornMap.get(item) == -1) {
            return;
        }
        if (numberOwnedMap.get(item) <= dirtyMap.get(item)) {
            return;
        }
        if (timesWornMap.get(item) + 1 == maxTimesWornMap.get(item)) {
            timesWornMap.put(item, 0);
            dirtyMap.put(item, dirtyMap.get(item) + 1);
        } else {
            timesWornMap.put(item, timesWornMap.get(item) + 1);
        }

        // regardless of the outcome, update the map
        String updateDirty = "UPDATE clothes SET number_dirty = :numberDirty, times_worn = :timesWorn WHERE (user_id = :userId AND specific_type = :specificType)";
        try (Connection conn = db.open()) {
            conn.createQuery(updateDirty)
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("clothes_id", "clothesId")
                .addColumnMapping("type", "type")
                .addColumnMapping("specific_type", "specificType")
                .addColumnMapping("number_owned", "numberOwned")
                .addColumnMapping("number_dirty", "numberDirty")
                .addColumnMapping("temp_high", "tempHigh")
                .addColumnMapping("temp_low", "tempLow")
                .addColumnMapping("times_worn", "timesWorn")
                .addColumnMapping("max_times_worn", "maxTimesWorn")
                .addParameter("specificType", item)
                .addParameter("numberDirty", dirtyMap.get(item))
                .addParameter("timesWorn", timesWornMap.get(item))
                .addParameter("userId", currId)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("ClothesService.evaluateDirty: Failed to update clothes", ex);
            throw new ClothesServiceException("ClothesService.evaluate: Failed to update clothes", ex);
        }
    }

    /**
    * Marks the items as dirty.
    * @param currId the id of the user.
    * @param body the json form of the items to be marked dirty, {top: a, pants: b, footwear: c, accessory: d, outerwear: e}.
    * @return true if the user should do laundry, false otherwise.
    */
    public boolean markDirty(int currId, String body) throws ClothesServiceException {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String top = obj.get("top").getAsString();
        String pants = obj.get("pants").getAsString();
        String footwear = obj.get("footwear").getAsString();
        String accessory = obj.get("accessory").getAsString();
        String outerwear = obj.get("outerwear").getAsString();

        // obtain the current count of dirty items
        String sqlTops = "SELECT * FROM clothes WHERE (user_id = :userId)";
        HashMap<String, Integer> dirtyMap = new HashMap<String, Integer>();
        HashMap<String, Integer> timesWornMap = new HashMap<String, Integer>();
        HashMap<String, Integer> maxTimesWornMap = new HashMap<String, Integer>();
        HashMap<String, Integer> numberOwnedMap = new HashMap<String, Integer>();
        try (Connection conn = db.open()) {
            List<Clothes> allClothes = 
                conn.createQuery(sqlTops)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("clothes_id", "clothesId")
                    .addColumnMapping("type", "type")
                    .addColumnMapping("specific_type", "specificType")
                    .addColumnMapping("number_owned", "numberOwned")
                    .addColumnMapping("number_dirty", "numberDirty")
                    .addColumnMapping("temp_high", "tempHigh")
                    .addColumnMapping("temp_low", "tempLow")
                    .addColumnMapping("times_worn", "timesWorn")
                    .addColumnMapping("max_times_worn", "maxTimesWorn")
                    .addParameter("userId", currId)
                    .executeAndFetch(Clothes.class);
            for (Clothes c : allClothes) {
                dirtyMap.put(c.getSpecificType(), c.getNumberDirty());
                timesWornMap.put(c.getSpecificType(), c.getTimesWorn());;
                maxTimesWornMap.put(c.getSpecificType(), c.getMaxTimesWorn());
                numberOwnedMap.put(c.getSpecificType(), c.getNumberOwned());
            }

        } catch (Sql2oException ex) {
            logger.error("ClothesService.markDirty: Failed to get clothes map", ex);
            throw new ClothesServiceException("ClothesService.markDirty: Failed to get clothes map", ex);
        }

        // update the value as necessary
        evaluateDirty(currId, dirtyMap, timesWornMap, maxTimesWornMap, numberOwnedMap, top);
        evaluateDirty(currId, dirtyMap, timesWornMap, maxTimesWornMap, numberOwnedMap, pants);
        evaluateDirty(currId, dirtyMap, timesWornMap, maxTimesWornMap, numberOwnedMap, footwear);
        evaluateDirty(currId, dirtyMap, timesWornMap, maxTimesWornMap, numberOwnedMap, accessory);
        evaluateDirty(currId, dirtyMap, timesWornMap, maxTimesWornMap, numberOwnedMap, outerwear);

        // then check for each item, if there is less than 30% clean, return true, to signal should do laundry
        for (HashMap.Entry<String, Integer> entry : dirtyMap.entrySet()) {
            if (numberOwnedMap.get(entry.getKey()) == 0) continue;
            // if we have more than 70% dirty, then we return true
            if (entry.getValue() > (0.7) * numberOwnedMap.get(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    /**
    * Resets the laundry of a user, resetting all dirty counts to 0.
    * @param currId the id of the user.
    */
    public void markClean(int currId) throws ClothesServiceException {
        // set all dirty fields to be 0
        String updateDirty = "UPDATE clothes SET number_dirty = :numberDirty, times_worn = :timesWorn WHERE user_id = :userId";
        
        try (Connection conn = db.open()) {
            conn.createQuery(updateDirty)
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("clothes_id", "clothesId")
                .addColumnMapping("type", "type")
                .addColumnMapping("specific_type", "specificType")
                .addColumnMapping("number_owned", "numberOwned")
                .addColumnMapping("number_dirty", "numberDirty")
                .addColumnMapping("temp_high", "tempHigh")
                .addColumnMapping("temp_low", "tempLow")
                .addColumnMapping("times_worn", "timesWorn")
                .addColumnMapping("max_times_worn", "maxTimesWorn")
                .addParameter("timesWorn", 0)
                .addParameter("numberDirty", 0)
                .addParameter("userId", currId)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("ClothesService.markClean: Failed to update clean clothes", ex);
            throw new ClothesServiceException("ClothesService.markClean: Failed to update clean clothes", ex);
        }
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class ClothesServiceException extends Exception {
        public ClothesServiceException(String message, Throwable cause) {
            super(message, cause);
        }
        public ClothesServiceException(String message) {
            super(message, null);
        }
    }

    public static class InvalidInputException extends Exception {
        public InvalidInputException(String message) {
            super(message, null);
        }
    }

}









