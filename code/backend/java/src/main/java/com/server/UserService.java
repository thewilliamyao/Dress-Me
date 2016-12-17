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
import java.util.PriorityQueue;
import java.util.Collections;

public class UserService {

    private Sql2o db;
    private static int userCounter = 0; // counter for free value of the id
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

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
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException("Failed to create schema at startup", ex);
        }
    }

    /**
    * Creates a new user, and adds it to the db. Also calls LocationService and ClothesService to
    * add defaults values into the location and clothes db.
    * @param body the json request form to create a user, {email: x, password: y}.
    * @return the java User object created.
    */
    public User createNewUser(String body) throws UserServiceException, LocationService.LocationServiceException,
            ClothesService.ClothesServiceException {
        User user = new Gson().fromJson(body, User.class);
	
    	// If no username was entered
    	if(user.getEmail().equals("")) {
    	    logger.error("UserService.createNewUser: No username specified.");
    	    throw new NewUserException("UserService.createNewUser: No username specified.");
    	} // If no password was entered
    	else if(user.getPassword().equals("")) {
    	    logger.error("UserService.createNewUser: No password specified.");
    	    throw new NewUserException("UserService.createNewUser: No password specified.");
    	}
	
        int currUserId = this.userCounter++;
        user.setUserId(currUserId);

        LocationService.createNewLocation(currUserId);
        ClothesService.createNewClothes(currUserId);

        // TODO password encryption/token authentication
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String currEmail = obj.get("email").getAsString();
        String currPassword = obj.get("password").getAsString();

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
                .addParameter("password", currPassword)
                .executeUpdate();
        } catch (Sql2oException ex) {
	        logger.error("UserService.createNewUser: Failed to add new user entry", ex);
            throw new UserServiceException("UserService.createNewUser: Failed to add new user entry", ex);
        }
        return user;
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

	// Criteria to compare Weather objects by: temperature, apparentTemp, humidity
	// Possibly windSpeed, precipIntensity?

	// When user gives feedback at the end of the day, front end should send comparison critera
	// to back end for storage in database.
	
	// Keep last 10, 20 days in database?	
	// Don't want to compare with large number of past days if used for a long time

	// TODO: replace this so it actually gets something from the database
	//       also replace it with combined Weather + Clothing object that has similarity index
	//       Create new class, use Pair<Weather, Outfit> (new Outfit class), or populate variables from database?
	ArrayList<DaySummary> pastDays = new ArrayList<>();

	// TODO: go through past weathers, populating a PriorityQueue of similarity indices (Doubles)
	//       make 20 into private static final variable "MAX_KEPT" or something
	PriorityQueue<Integer> similarityIndices = new PriorityQueue<>(20, Collections.reverseOrder());
	HashMap<Integer, DaySummary> similarList = new HashMap<>();

	for (DaySummary d : pastDays) {
	    // Compare to current weather
	    Integer similarity = Integer.valueOf(currWeather.compareTo(d));
	    similarityIndices.add(similarity);
	    similarList.put(similarity, d);
	}
		
        // recreate clothes map for this user
        List<Clothes> currClothes = ClothesService.getClothesList(currId);
        HashMap<String, Double> highMap = new HashMap<String, Double>();
        HashMap<String, Double> lowMap = new HashMap<String, Double>();
        HashMap<String, Integer> ownedMap = new HashMap<String, Integer>();
        HashMap<String, Integer> dirtyMap = new HashMap<String, Integer>();
        for (Clothes c : currClothes) {
            ownedMap.put(c.getSpecificType(), new Integer(c.getNumberOwned()));
            dirtyMap.put(c.getSpecificType(), new Integer(c.getNumberDirty()));
            highMap.put(c.getSpecificType(), new Double(c.getTempHigh()));
            lowMap.put(c.getSpecificType(), new Double(c.getTempLow()));
        }		
	
        String pantsRecommendation = "NONE";
        String backupPants = "";
        String shirtRecommendation = "NONE";
        String backupShirt = "";
        String outwearRecommendation = "NONE";
        String backupOuterwear = "";
        String footwearRecommendation = "NONE";
        String backupFootwear = "";
        String accessoryRecommendation = "NONE";

        // if too hot for long pants, recommend shorts
        if (currWeather.getMaxApparentTemp() > highMap.get("long_pants")) {
            // recommend shorts if possible
            if (hasClean("shorts", ownedMap, dirtyMap)) {
                pantsRecommendation = "shorts";
                if (hasClean("long_pants", ownedMap, dirtyMap)) {
                    // we can recommend long pants as a backup
                    backupPants = "long_pants";
                }
            } else if (hasClean("long_pants", ownedMap, dirtyMap)) {
                // if not recommend long pants if he owns it
                pantsRecommendation = "long_pants";
            } else {
                // he has no pants...
                pantsRecommendation = "NONE";
            }
            // recommend 
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
        if (currWeather.getMaxApparentTemp() > lowMap.get("tank_top")) {
            // want to prefer tank
            if (hasClean("tank_top", ownedMap, dirtyMap)) {
                shirtRecommendation = "tank_top";
                if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                    backupShirt = "t_shirt";
                } else if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                    backupShirt = "long_sleeve";
                }
            } else if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                // if owned none, then shirt
                shirtRecommendation = "t_shirt";
                if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                    backupShirt = "long_sleeve";
                }
            } else if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                // if owned none, then long sleeve
                shirtRecommendation = "long_sleeve";
                if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                    backupShirt = "t_shirt";
                }
            }
        } else if (currWeather.getMaxApparentTemp() < highMap.get("long_sleeve")) { // TODO: double check this
            // it is cold enough for a long sleeve
            if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                shirtRecommendation = "long_sleeve";
                if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                    backupShirt = "t_shirt";
                }
            } else if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                shirtRecommendation = "t_shirt";
            } else if (hasClean("tank_top", ownedMap, dirtyMap)) {
                shirtRecommendation = "tank_top";
            }
        } else {
            // recommend a t-shirt
            if (hasClean("t_shirt", ownedMap, dirtyMap)) {
                shirtRecommendation = "t_shirt";
                if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                    backupShirt = "long_sleeve";
                }
            } else if (hasClean("long_sleeve", ownedMap, dirtyMap)) {
                shirtRecommendation = "long_sleeve";
            } else if (hasClean("tank_top", ownedMap, dirtyMap)) {
                shirtRecommendation = "tank_top";
            }
        }

        // set footwear.
            //check precip. if raining or snowing, recommend boots. if not, but still cold, recommend shoes. if hot and clear, recommend sandals.
        if (currWeather.getPrecipType().equals("rain") || currWeather.getPrecipType().equals("snow") || (currWeather.getMaxApparentTemp() < lowMap.get("boots"))) {
            // wear boots
            if (ownedMap.get("boots") > 0) {
                footwearRecommendation = "boots";
                if (ownedMap.get("shoes") > 0) {
                    backupFootwear = "shoes";
                }
            } else if (ownedMap.get("shoes") > 0) {
                footwearRecommendation = "shoes";
            } else if (ownedMap.get("sandals") > 0) {
                footwearRecommendation = "sandals";
            }
        } else if (currWeather.getMaxApparentTemp() > lowMap.get("sandals")) {
            // really hot, wear sandals
            if (ownedMap.get("sandals") > 0) {
                footwearRecommendation = "sandals";
                if (ownedMap.get("shoes") > 0) {
                    backupFootwear = "shoes";
                }
            } else if (ownedMap.get("shoes") > 0) {
                footwearRecommendation = "shoes";
            } else if (ownedMap.get("boots") > 0) {
                footwearRecommendation = "boots";
            }
        } else {
            // recommend shoes
            if (ownedMap.get("shoes") > 0) {
                footwearRecommendation = "shoes";
            } else if (ownedMap.get("boots") > 0) {
                footwearRecommendation = "boots";
            } else if (ownedMap.get("sandals") > 0) {
                footwearRecommendation = "sandals";
            }
        }

        // set outerwear.
            // check precip. if very cold, recommend winter jacket. else if rainig, recommend rain jacket. if clear but somewhat cold, recommend hoodie.
        if (currWeather.getMaxApparentTemp() < highMap.get("winter_coat") || currWeather.getPrecipType().equals("snow")) {
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
        } else if (currWeather.getWindSpeed() > 20) {
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
        } else if (currWeather.getPrecipType().equals("rain")) {
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
        } else if (currWeather.getMaxApparentTemp() < highMap.get("hoodie")) {
            // recommend hoodie/sweater
            if (hasClean("hoodie", ownedMap, dirtyMap)) {
                outwearRecommendation = "hoodie";
                if (hasClean("sweater", ownedMap, dirtyMap)) {
                    backupOuterwear = "sweater";
                }
            } else if (hasClean("sweater", ownedMap, dirtyMap)) {
                outwearRecommendation = "sweater";
                if (hasClean("hoodie", ownedMap, dirtyMap)) {
                    backupOuterwear = "hoodie";
                }
            }
        }

        // set accessory
            // if windy or cold, scarf. if raining and not windy, recommend umbrella.
            // prefer umbrella to scarf.
        if (currWeather.getPrecipType().equals("rain") && currWeather.getWindSpeed() < 30) {
            if (ownedMap.get("umbrella") > 0) {
                accessoryRecommendation = "umbrella";
            }
        } else if (currWeather.getMaxApparentTemp() < highMap.get("scarf") || currWeather.getWindSpeed() > 20) {
            if (ownedMap.get("scarf") > 0) {
                accessoryRecommendation = "scarf";
            }
        }

        HashMap<String, Recommendation> toReturn = new HashMap<String, Recommendation>();
        // ArrayList<Recommendation> toReturn = new ArrayList<Recommendation>();
        // first recommendation
        toReturn.put("FirstRecommendation", new Recommendation(shirtRecommendation, pantsRecommendation, footwearRecommendation, accessoryRecommendation, outwearRecommendation));
  
        //second recommendation
        // swap out shirt if possible
        if (!backupShirt.equals("")) {
            toReturn.put("SecondRecommendation", new Recommendation(backupShirt, pantsRecommendation, footwearRecommendation, accessoryRecommendation, outwearRecommendation));
        } else if (!backupPants.equals("")) {
            toReturn.put("SecondRecommendation", new Recommendation(shirtRecommendation, backupPants, footwearRecommendation, accessoryRecommendation, outwearRecommendation));
        } else if (!backupOuterwear.equals("")) {
            toReturn.put("SecondRecommendation", new Recommendation(shirtRecommendation, pantsRecommendation, footwearRecommendation, accessoryRecommendation, backupOuterwear));
        } else if (!backupFootwear.equals("")) {
            toReturn.put("SecondRecommendation", new Recommendation(shirtRecommendation, pantsRecommendation, backupFootwear, accessoryRecommendation, outwearRecommendation));
        }

        // third recommendation
        if (!backupShirt.equals("") && !backupPants.equals("")) {
            toReturn.put("ThirdRecommendation", new Recommendation(backupShirt, backupPants, footwearRecommendation, accessoryRecommendation, outwearRecommendation));
        } else if (!backupShirt.equals("") && !backupOuterwear.equals("")) {
            toReturn.put("ThirdRecommendation", new Recommendation(backupShirt, pantsRecommendation, footwearRecommendation, accessoryRecommendation, backupOuterwear));
        } else if (!backupOuterwear.equals("") && !backupPants.equals("")) {
            toReturn.put("ThirdRecommendation", new Recommendation(shirtRecommendation, backupPants, footwearRecommendation, accessoryRecommendation, backupOuterwear));
        } else if (!backupFootwear.equals("") && !backupOuterwear.equals("")) {
            toReturn.put("ThirdRecommendation", new Recommendation(shirtRecommendation, pantsRecommendation, backupFootwear, accessoryRecommendation, backupOuterwear));
        } else if (!backupFootwear.equals("") && !backupShirt.equals("")) {
            toReturn.put("ThirdRecommendation", new Recommendation(backupShirt, pantsRecommendation, backupFootwear, accessoryRecommendation, backupOuterwear));
        } else if (!backupFootwear.equals("") && !backupPants.equals("")) {
            toReturn.put("ThirdRecommendation", new Recommendation(shirtRecommendation, backupPants, backupFootwear, accessoryRecommendation, backupOuterwear));
        }

        return toReturn;
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public boolean hasClean(String item, HashMap<String, Integer> ownedMap,
			    HashMap<String, Integer> dirtyMap) {
	return ownedMap.get(item) - dirtyMap.get(item) > 0;
    }

    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class NewUserException extends UserServiceException {
    	public NewUserException(String message) {
    	    super(message, null);
    	}
    }
}
