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
import java.util.TreeMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;

// imports for encrypting password
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class UserService {

    private static final int MAX_RECS = 3;
    private static final int MAX_DAYS = 32;
    private static final int MAX_LOOPS = 10;
    private Sql2o db;
    private static int userCounter = 0; // counter for free value of the id
    private static int dayCounter = 0; // counter for free value of the id
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    /*
        global vars for encrypting password:
    */
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };
    private static final char[] SECRET_KEY = "skhafiluasdhfuihasd".toCharArray();
    private static final String PBE_STRING = "PBEWithMD5AndDES";
    /**
    * Construct the model with a pre-defined datasource. The current implementation
    * also ensures that the DB schema is created if necessary.
    *
    * @param currDb the sq2o object containing the connection to the database.
    */
    public UserService(Sql2o currDb) throws UserServiceException {
        db = currDb;
        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            // create table for user
            String sqlUser = "CREATE TABLE IF NOT EXISTS users (user_id INTEGER PRIMARY KEY, " +
                         "                                 email TEXT, password TEXT)" ;
            conn.createQuery(sqlUser).executeUpdate();
            String sqlUserId = "SELECT MAX(user_id) FROM users";
            Integer latestUser = conn.createQuery(sqlUserId)
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("email", "email")
                .addColumnMapping("password", "password")
                .executeAndFetchFirst(Integer.class);
            if (latestUser != null) {
                userCounter = latestUser.intValue() + 1;
            } else {
                userCounter = 0;
            }
            // create table for daySummary
            String sqlSummary = "CREATE TABLE IF NOT EXISTS day_summaries (day_summary_id INTEGER PRIMARY KEY, user_id INTEGER, " +
                            "                               max_temp DECIMAL, max_apparent_temp DECIMAL, top TEXT, pants TEXT, " +
                            "                               footwear TEXT, accessory TEXT, outerwear TEXT)";
            conn.createQuery(sqlSummary).executeUpdate();
            String sqlSummaryId = "SELECT MAX(day_summary_id) FROM day_summaries";
            Integer latestDaySummary = conn.createQuery(sqlSummaryId)
                                    .addColumnMapping("day_summary_id", "daySummaryId")
                                    .addColumnMapping("user_id", "userId")
                                    .addColumnMapping("max_temp", "maxTemp")
                                    .addColumnMapping("max_apparent_temp", "maxApparentTemp")
                                    .addColumnMapping("top", "top")
                                    .addColumnMapping("pants", "pants")
                                    .addColumnMapping("footwear", "footwear")
                                    .addColumnMapping("accessory", "accessory")
                                    .addColumnMapping("outerwear", "outerwear")
                                    .executeAndFetchFirst(Integer.class);
            if (latestDaySummary != null) {
                dayCounter = latestDaySummary.intValue() + 1;
            } else {
                dayCounter = 0;
            }
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException("Failed to create schema at startup", ex);
        }

    }

    public void reset() throws UserServiceException {
        try (Connection conn = db.open()) {
            String sql = "DROP TABLE IF EXISTS users";
            conn.createQuery(sql).executeUpdate();
            sql = "DROP TABLE IF EXISTS clothes";
            conn.createQuery(sql).executeUpdate();
            sql = "DROP TABLE IF EXISTS locations";
            conn.createQuery(sql).executeUpdate();
        }
    }

    private static String base64Encode(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }
    private static byte[] base64Decode(String input) throws IOException {
        return new BASE64Decoder().decodeBuffer(input);
    }

    /**
    * Helper method to encrypt an input
    * @param input the string which you want to encrypt.
    * @return the encrypted string
    */
    public String encrypt(String input) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_STRING);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET_KEY));
        Cipher pbeCipher = Cipher.getInstance(PBE_STRING);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(input.getBytes("UTF-8")));
    }

    /**
    * Helper method to decrypt a string.
    * @param input the string which you wish to decrypt
    * @return the decrypted string
    */
    public String decrypt(String input) throws GeneralSecurityException, IOException{
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_STRING);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET_KEY));
        Cipher pbeCipher = Cipher.getInstance(PBE_STRING);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(input)), "UTF-8");
    }
    
    /**
    * Creates a new user, and adds it to the db. Also calls LocationService and ClothesService to
    * add defaults values into the location and clothes db.
    * @param body the json request form to create a user, {email: x, password: y}.
    * @return the java User object created.
    */
    public LoginToken createNewUser(String body) throws NewUserException, UserServiceException, LocationService.LocationServiceException,
            ClothesService.ClothesServiceException, GeneralSecurityException, IOException, Exception {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String currEmail = obj.get("email").getAsString();
        String currPassword = obj.get("password").getAsString();

        if(currEmail.equals("")) {
            logger.error("UserService.createNewUser: No username specified.");
            return new LoginToken(-1, "");
        } else if(currPassword.equals("")) {
            logger.error("UserService.createNewUser: No password specified.");
            return new LoginToken(-1, "");
        }
    
        // check that there are no users with the same email
        String sqlUserCount = "SELECT COUNT(*) FROM users WHERE (email = :email)";
        int numUsers = 0;
        try (Connection conn = db.open()) {
            numUsers = 
                conn.createQuery(sqlUserCount)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("email", "email")
                    .addColumnMapping("password", "password")
                    .addParameter("email", currEmail)
                    .executeAndFetchFirst(Integer.class);
        } catch (Sql2oException ex) {
            logger.error("UserService.createNewUser: Failed to query users", ex);
            return new LoginToken(-2, "");
        }
        if (numUsers > 0) {
            logger.error("UserServer.createNewUser: User already exists");
            return new LoginToken(-1, "");
        }
        int currUserId = this.userCounter++;
        LocationService.createNewLocation(currUserId);
        ClothesService.createNewClothes(currUserId);

        String encryptedPass  = encrypt(currPassword);

        // add user to database
        String sqlUser = "INSERT INTO users (user_id, email, password)" +
            "                  VALUES (:userId, :email, :password)";

        try (Connection conn = db.open()) {
            conn.createQuery(sqlUser)
                .addColumnMapping("user_id", "userId")
                .addColumnMapping("email", "email")
                .addColumnMapping("password", "password")
                .addParameter("userId", currUserId)
                .addParameter("email", currEmail)
                .addParameter("password", encryptedPass)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("UserService.createNewUser: Failed to add new user entry", ex);
            return new LoginToken(-2, "");
        }
        return new LoginToken(currUserId);
    }

    public LoginToken getLoginToken(String body) throws UserServiceException, Exception { 
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String currEmail = obj.get("email").getAsString();
        String currPassword = obj.get("password").getAsString();

        if(currEmail.equals("")) {
            logger.error("UserService.createNewUser: No username specified.");
            return new LoginToken(-1, "");
        } else if(currPassword.equals("")) {
            logger.error("UserService.createNewUser: No password specified.");
            return new LoginToken(-1, "");
        }
        // get encrypted password
        String sqlUser = "SELECT * FROM users WHERE (email = :email)";
        User currUser;
        try (Connection conn = db.open()) {
            currUser =
                conn.createQuery(sqlUser)
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("email", "email")
                    .addColumnMapping("password", "password")
                    .addParameter("email", currEmail)
                    .executeAndFetchFirst(User.class);
        } catch (Sql2oException ex) {
            logger.error("UserService.createLoginToken: Failed to find user entry", ex);
            return new LoginToken(-2, "");
        }
        if (currUser == null) {
            return new LoginToken(-1, "");
        }
        // now verify password
        try {
            if (currPassword.equals(decrypt(currUser.getPassword()))) {
                // good, generate token
                return new LoginToken(currUser.getUserId());
            }
        } catch (Exception ex) {
            logger.error("UserServer.createLoginToken: Invalid password", ex);
            return new LoginToken(-1, "");
        }
        return new LoginToken(-1, "");
    }


    /**
    * Creates a map of 3 recommendations based on a user's location and closet.
    * @param currId, the id of the user.
    * @return a map of FirstRecommendation, SecondRecommendation, and ThirdRecommendation to the respective recommendations.
    */
    public HashMap<String, Recommendation> getRecommendation(int currId) throws UserServiceException, LocationService.LocationServiceException,
            ClothesService.ClothesServiceException {
        // get the location lat and long
        Location currLocation = LocationService.getLocation(currId);

        // get the weather based on the location
        Weather currWeather = new Weather(currLocation.getLatitude(), currLocation.getLongitude());

        // recreate clothes map for this user   
        List<Clothes> currClothes = ClothesService.getClothesList(currId);        
        HashMap<Clothes, Integer> ownedMap = new HashMap<>();
        HashMap<Clothes, Integer> dirtyMap = new HashMap<>();
    
        for (Clothes c : currClothes) {
            ownedMap.put(c, new Integer(c.getNumberOwned()));
            dirtyMap.put(c, new Integer(c.getNumberDirty()));            
        }

        // When user gives feedback at the end of the day, front end should send comparison critera
        // to back end for storage in database.
            
        // get daySummary from database
        String sqlDaySummary = "SELECT * FROM day_summaries WHERE (user_id = :userId)";
        List<DaySummary> pastDays;
        try (Connection conn = db.open()) {
            pastDays = 
                conn.createQuery(sqlDaySummary)
                    .addColumnMapping("day_summary_id", "daySummaryId")
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("max_temp", "maxTemp")
                    .addColumnMapping("max_apparent_temp", "maxApparentTemp")
                    .addColumnMapping("top", "top")
                    .addColumnMapping("pants", "pants")
                    .addColumnMapping("footwear", "footwear")
                    .addColumnMapping("accessory", "accessory")
                    .addColumnMapping("outerwear", "outerwear")
                    .addParameter("userId", currId)
                    .executeAndFetch(DaySummary.class);
        } catch (Sql2oException ex) {
            logger.error("UserService.getRecommendation: Failed to get day_summaries", ex);
            throw new UserServiceException("UserService.giveFeedback: Failed to get day_summaries", ex);
        }
        
        // ArrayList<DaySummary> pastDays = new ArrayList<>();
        HashMap<String, Recommendation> toReturn = new HashMap<String, Recommendation>();
        PriorityQueue<Integer> similarityQueue = new PriorityQueue<>(MAX_DAYS);
        HashMap<Integer, DaySummary> similarList = new HashMap<>();

        for (DaySummary d : pastDays) {
            // Compare to current weather
            Integer similarity = Integer.valueOf(currWeather.compareTo(d));
	    if (similarity < 10) {
		similarityQueue.add(similarity);
		similarList.put(similarity, d);
	    }
        }

        Integer head = similarityQueue.poll();
        String recNumber = "FirstRecommendation";
        Recommendation outfit;  
        
        while (head != null && toReturn.size() < MAX_RECS) {
            if (toReturn.size() == 1) {
                recNumber = "SecondRecommendation";
            } else if (!toReturn.isEmpty()) {
                recNumber = "ThirdRecommendation";
            }
            
            DaySummary day = similarList.get(head);
            outfit = day.getRecommendation();
            
            if (hasOutfit(outfit, ownedMap, dirtyMap) && !toReturn.containsValue(outfit)) {
                if (!outfit.getOuterwear().equals("winter_coat")) {
                    if (ownedMap.containsKey("windbreaker") && currWeather.getWindSpeed() > 25) {
                        outfit.setOuterwear("windbreaker");
                    } // Add more cases like this
                }     // LMFAO is it really worth it tho
                toReturn.put(recNumber, outfit);
            } else {
                toReturn.put(recNumber, mutateRecommendation(outfit, currWeather.getMaxApparentTemp(),
                                     toReturn, ownedMap, dirtyMap));
            }
            head = similarityQueue.poll();
        }
        

        int loops = 0;
        // If there aren't enough entries in the database or there are no entries with similar weather,
        // make recommendations using the default algorithm.
        while (toReturn.size() < MAX_RECS) {
            if (loops >= MAX_LOOPS) {
                break;
            }
            if (toReturn.size() == 1) {
                recNumber = "SecondRecommendation";
            } else if (!toReturn.isEmpty()) {
                recNumber = "ThirdRecommendation";
            }
            // Make default recommendation if we weren't able to recommend from smart algorithm
            outfit = defaultRecommendation(toReturn, ownedMap, dirtyMap, currWeather);
            if (!toReturn.containsValue(outfit)) {
                toReturn.put(recNumber, outfit);
            } else {
                toReturn.put(recNumber, mutateRecommendation(outfit, currWeather.getMaxApparentTemp(),
                                     toReturn, ownedMap, dirtyMap));
            }
            loops++;
        }
        return toReturn;
    }

    public Recommendation mutateRecommendation(Recommendation outfit, Double targetTemp,
              HashMap<String, Recommendation> currRecs, HashMap<Clothes, Integer> ownedMap,
              HashMap<Clothes, Integer> dirtyMap) {
        // create TreeMaps to get clothes w/ similar temperature parameters
        TreeMap<Double, List<Clothes>> highTree = new TreeMap<>();
        TreeMap<Double, List<Clothes>> lowTree = new TreeMap<>();

        // Populate high/low trees; if a collision occurs, add to list instead of replacing.
        for (Clothes c : ownedMap.keySet()) {
            if (hasClean(c, ownedMap, dirtyMap)) {
                if (!highTree.containsKey(Double.valueOf(c.getTempHigh()))) {
                    highTree.put(new Double(c.getTempHigh()), new ArrayList<Clothes>(Arrays.asList(c)));
                } else {
                    highTree.get(c.getTempHigh()).add(c);
                }
                if (!lowTree.containsKey(Double.valueOf(c.getTempLow()))) {
                    lowTree.put(new Double(c.getTempLow()), new ArrayList<Clothes>(Arrays.asList(c)));
                } else {
                    lowTree.get(c.getTempLow()).add(c);
                }
            }
        }


        for (Clothes item : outfit.asClothesList()) {
            if (!hasClean(item, ownedMap, dirtyMap) || currRecs.containsValue(outfit)) {
                Double highKey, lowKey;
                List<Clothes> options = new ArrayList<>();
                Clothes selection;
                highKey = highTree.higherKey(targetTemp);
                lowKey = lowTree.lowerKey(targetTemp);
                // prioritize suggestions based on using tree
                while (highKey != null && !highTree.get(highKey).get(0).getType().equals(item.getType())) {
                    highKey = highTree.higherKey(highKey);
                }
                while (lowKey != null && !lowTree.get(lowKey).get(0).getType().equals(item.getType())) {
                    lowKey = lowTree.lowerKey(lowKey);
                }
                if (highKey != null) {
                    options.addAll(highTree.get(highKey));
                }
                if (lowKey != null) {
                    options.addAll(lowTree.get(lowKey));
                }
                // but if no good options, just take things that are clean and work with that
                if (options.size() == 0) {
                    for (Clothes c : ownedMap.keySet()) {
                        if (hasClean(c, ownedMap, dirtyMap) && item.getType().equals(c.getType())) {
                            options.add(c);
                        }
                    }
                }
                if (options.size() != 0) {
                    selection = options.get(0);
                    double minDiff = Math.min(Math.abs(selection.getTempHigh() - targetTemp),
                               Math.abs(selection.getTempLow() - targetTemp));
                    double diff;
                    for (Clothes c : options) {
                        if (hasClean(c, ownedMap, dirtyMap) && c.getType().equals(item.getType()) && !c.getSpecificType().equals(item.getSpecificType())) {
                            if (c.getTempHigh() >= targetTemp && c.getTempLow() <= targetTemp && !outfit.hasItem(c)) {
                                selection = c;
                                break;
                            } else {
                                diff = Math.min(Math.abs(selection.getTempHigh() - targetTemp),
                                Math.abs(selection.getTempLow() - targetTemp));
                                if (outfit.hasItem(c) || diff < minDiff) {
                                    selection = c;
                                    minDiff = diff;
                                }
                            }
                        }
                    }               
                } else { // No possible replacements left. Recommend original.
                    selection = new Clothes("NONE", "NONE");
                }
                outfit.setItem(selection);
            }
        }
        return outfit;
    }

    public Recommendation defaultRecommendation(HashMap<String, Recommendation> currRecs,
              HashMap<Clothes, Integer> ownedMap, HashMap<Clothes, Integer> dirtyMap,
                        Weather weather) {
    
        String pantsRecommendation = "NONE";
        String shirtRecommendation = "NONE";
        String outwearRecommendation = "NONE";
        String footwearRecommendation = "NONE";
        String accessoryRecommendation = "NONE";
        Double temp = weather.getMaxApparentTemp();

        HashMap<String, Double> highMap = new HashMap<>();
        HashMap<String, Double> lowMap = new HashMap<>();

        for (Clothes c : ownedMap.keySet()) {
            highMap.put(c.getSpecificType(), new Double(c.getTempHigh()));
            lowMap.put(c.getSpecificType(), new Double(c.getTempLow()));            
        }
    
        // if too hot for long pants, recommend shorts
        if (temp > highMap.get("long_pants")) {
            // recommend shorts if possible
            if (hasClean("shorts", ownedMap, dirtyMap)) {
                pantsRecommendation = "shorts";
            } else if (hasClean("long_pants", ownedMap, dirtyMap)) {
                // if not recommend long pants if he owns it
                pantsRecommendation = "long_pants";
            } else {
                // he has no pants...
                pantsRecommendation = "NONE";
            }       
        } else {
            // we prefer long pants
            if (hasClean("long_pants", ownedMap, dirtyMap)) {
                pantsRecommendation = "long_pants";
            } else if (hasClean("shorts", ownedMap, dirtyMap)) {
                // if not recommend long pants if he owns it
                pantsRecommendation = "shorts";
            } else {
                // he has no pants...
                pantsRecommendation = "NONE";
            }
        }

        // set shirt.
        //if very hot, recommend tank. if somewhat cold, recommend long sleeve. either way, backup is t-shirt
        if (temp > lowMap.get("tank_top")) {
            // want to prefer tank
            if (hasClean("tank_top", ownedMap, dirtyMap)) {
                shirtRecommendation = "tank_top";                
            } else if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                // if owned none, then shirt
                shirtRecommendation = "t_shirt";
            } else if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                // if owned none, then long sleeve
                shirtRecommendation = "long_sleeve";        
            }
        } else if (temp < highMap.get("long_sleeve")) { // TODO: double check this
            // it is cold enough for a long sleeve
            if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                shirtRecommendation = "long_sleeve";
            } else if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                shirtRecommendation = "t_shirt";
            } else if (hasClean("tank_top", ownedMap, dirtyMap)) {
                shirtRecommendation = "tank_top";
            }
        } else {
            // recommend a t-shirt
            if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                shirtRecommendation = "t_shirt";              
            } else if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                shirtRecommendation = "long_sleeve";
            } else if (hasClean("tank_top", ownedMap, dirtyMap)) {
                shirtRecommendation = "tank_top";
            }
        }

        // set footwear.
        // check precip. if raining or snowing, recommend boots
        // if not, but still cold, recommend shoes. if hot and clear, recommend sandals.
        if (weather.getPrecipType().equals("rain") || weather.getPrecipType().equals("snow")) {
            // wear boots
            if (hasClean("boots", ownedMap, dirtyMap)) {
                footwearRecommendation = "boots";
            } else if (hasClean("shoes", ownedMap, dirtyMap)) {
                footwearRecommendation = "shoes";
            } else if (hasClean("sandals", ownedMap, dirtyMap)) {
                footwearRecommendation = "sandals";
            }
        } else if (weather.getMaxApparentTemp() > lowMap.get("sandals")) {
            // really hot, wear sandals
            if (hasClean("sandals", ownedMap, dirtyMap)) {
                footwearRecommendation = "sandals";            
            } else if (hasClean("shoes", ownedMap, dirtyMap)) {
                footwearRecommendation = "shoes";
            } else if (hasClean("boots", ownedMap, dirtyMap)) {
                footwearRecommendation = "boots";
            }
        } else {
            // recommend shoes
            if (hasClean("shoes", ownedMap, dirtyMap)) {
                footwearRecommendation = "shoes";
            } else if (hasClean("boots", ownedMap, dirtyMap)) {
                footwearRecommendation = "boots";
            } else if (hasClean("sandals", ownedMap, dirtyMap)) {
                footwearRecommendation = "sandals";
            }
        }

        // set outerwear.
        // check precip. if very cold, recommend winter jacket.
        // else if rainig, recommend rain jacket. if clear but somewhat cold, recommend hoodie.
        if (temp < highMap.get("winter_coat") || weather.getPrecipType().equals("snow")) {
            // recommend winter coat
            if (hasClean("winter_coat", ownedMap, dirtyMap)) {
                outwearRecommendation = "winter_coat";
            } else if (hasClean("hoodie", ownedMap, dirtyMap)) {
                outwearRecommendation = "hoodie";
            } else if (hasClean("sweater", ownedMap, dirtyMap)) {
                outwearRecommendation = "sweater";
            } else if (hasClean("windbreaker", ownedMap, dirtyMap)) {
                outwearRecommendation = "windbreaker";
            } else if (hasClean("rain_jacket", ownedMap, dirtyMap)) {
                outwearRecommendation = "rain_jacket";
            }
        } else if (weather.getWindSpeed() > 20) {
            // if windy, recommend windbreaker
            if (hasClean("windbreaker", ownedMap, dirtyMap)) {
                outwearRecommendation = "windbreaker";
            } else if (hasClean("rain_jacket", ownedMap, dirtyMap)) {
                outwearRecommendation = "rain_jacket";
            } else if (hasClean("hoodie", ownedMap, dirtyMap)) {
                outwearRecommendation = "hoodie";
            } else if (hasClean("sweater", ownedMap, dirtyMap)) {
                outwearRecommendation = "sweater";
            }
        } else if (weather.getPrecipType().equals("rain")) {
            // if rainy, recommend rain jacket
            if (hasClean("rain_jacket", ownedMap, dirtyMap)) {
                outwearRecommendation = "rain_jacket";
            } else if (hasClean("windbreaker", ownedMap, dirtyMap)) {
                outwearRecommendation = "windbreaker";
            } else if (hasClean("hoodie", ownedMap, dirtyMap)) {
                outwearRecommendation = "hoodie";
            } else if (hasClean("sweater", ownedMap, dirtyMap)) {
                outwearRecommendation = "sweater";
            }
        } else if (weather.getMaxApparentTemp() < highMap.get("hoodie")) {
            // recommend hoodie/sweater
            if (hasClean("hoodie", ownedMap, dirtyMap)) {
                outwearRecommendation = "hoodie";                
            } else if (hasClean("sweater", ownedMap, dirtyMap)) {
                outwearRecommendation = "sweater";
            }
        }

        // set accessory
        // if windy or cold, scarf. if raining and not windy, recommend umbrella.
        // prefer umbrella to scarf.
        if (weather.getPrecipType().equals("rain") && weather.getWindSpeed() < 30) {
            if (hasClean("umbrella", ownedMap, dirtyMap)) {
                accessoryRecommendation = "umbrella";
            }
        } else if (temp < highMap.get("scarf") || weather.getWindSpeed() > 20) {
            if (hasClean("scarf", ownedMap, dirtyMap)) {
                accessoryRecommendation = "scarf";
            }
        }
        return new Recommendation(shirtRecommendation, pantsRecommendation, footwearRecommendation,
                      accessoryRecommendation, outwearRecommendation);  
    }

    public void giveFeedback(int currId, String body) throws LocationService.LocationServiceException, UserServiceException {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        // grab recommendation
        String top = obj.get("top").getAsString();
        String pants = obj.get("pants").getAsString();
        String footwear = obj.get("footwear").getAsString();
        String accessory = obj.get("accessory").getAsString();
        String outerwear = obj.get("outerwear").getAsString();
        // get adjustment value
        double adjustment = obj.get("adjustment").getAsDouble();
        // now fetch the weather for the day
        Location currLocation = LocationService.getLocation(currId);
        Weather currWeather = new Weather(currLocation.getLatitude(), currLocation.getLongitude());
        // now store the daySummary
        try (Connection conn = db.open()) {
            String summaryUpdate = "INSERT INTO day_summaries (day_summary_id, user_id, max_temp, max_apparent_temp, top, pants, footwear, accessory, outerwear)" + "VALUES (:daySummaryId, :userId, :maxTemp, :maxApparentTemp, :top, :pants, :footwear, :accessory, :outerwear)";
            conn.createQuery(summaryUpdate)
                    .addColumnMapping("day_summary_id", "daySummaryId")
                    .addColumnMapping("user_id", "userId")
                    .addColumnMapping("max_temp", "maxTemp")
                    .addColumnMapping("max_apparent_temp", "maxApparentTemp")
                    .addColumnMapping("top", "top")
                    .addColumnMapping("pants", "pants")
                    .addColumnMapping("footwear", "footwear")
                    .addColumnMapping("accessory", "accessory")
                    .addColumnMapping("outerwear", "outerwear")
                    .addParameter("daySummaryId", dayCounter++)
                    .addParameter("userId", currId)
                    .addParameter("maxTemp", currWeather.getMaxTemp() + adjustment)
                    .addParameter("maxApparentTemp", currWeather.getMaxApparentTemp() + adjustment)
                    .addParameter("top", top)
                    .addParameter("pants", pants)
                    .addParameter("footwear", footwear)
                    .addParameter("accessory", accessory)
                    .addParameter("outerwear", outerwear)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("UserService.giveFeedback: Failed to add day_summmary");
            throw new UserServiceException("UserService.giveFeedback: Failed to add day_summary", ex);
        }
        // fetch recommendation
        // adjustment value
    }
    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//
    
    public boolean hasClean(Clothes item, HashMap<Clothes, Integer> ownedMap,
                HashMap<Clothes, Integer> dirtyMap) {
        if (item.getSpecificType().equals("NONE")) return true;
        return (ownedMap.get(item) - dirtyMap.get(item)) > 0;
    }

    public boolean hasClean(String item, HashMap<Clothes, Integer> ownedMap,
                HashMap<Clothes, Integer> dirtyMap) {
        if (item.equals("NONE")) return true;
        Clothes dummy = new Clothes(item);
        return (ownedMap.get(dummy) - dirtyMap.get(dummy)) > 0;
    }

    public boolean hasOutfit(Recommendation outfit, HashMap<Clothes, Integer> ownedMap, HashMap<Clothes, Integer> dirtyMap) {
    List<Clothes> clothes = outfit.asClothesList();
        for (Clothes item : clothes) {
            if (!hasClean(item, ownedMap, dirtyMap)) {
            return false;
            }
        }
        return true;
    }

    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
        public UserServiceException(String message) {
            super(message, null);
        }
    }

    public static class NewUserException extends Exception {
        public NewUserException(String message) {
            super(message, null);
        }
    }
}
