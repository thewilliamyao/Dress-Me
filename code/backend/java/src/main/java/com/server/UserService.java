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

    private Sql2o db;
    private static int userCounter = 0; // counter for free value of the id
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


    private static String base64Encode(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }
    private static byte[] base64Decode(String input) throws IOException {
        return new BASE64Decoder().decodeBuffer(input);
    }

    /**

    */
    public String encrypt(String input) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_STRING);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET_KEY));
        Cipher pbeCipher = Cipher.getInstance(PBE_STRING);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(input.getBytes("UTF-8")));
    }

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
    public User createNewUser(String body) throws UserServiceException, LocationService.LocationServiceException,
            ClothesService.ClothesServiceException, GeneralSecurityException, IOException {
        User user = new Gson().fromJson(body, User.class);
	    String tmp = encrypt(user.getPassword());
        System.out.println("----------------------------------");
        System.out.printf("Expected: %s\n", user.getPassword());
        System.out.printf("Encrypted: %s\n", tmp);
        System.out.printf("Decrypted: %s\n", decrypt(tmp));
        System.out.println("----------------------------------");
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
            if (ownedMap.get("shorts") - dirtyMap.get("shorts") > 0) {
                pantsRecommendation = "shorts";
                if ((ownedMap.get("long_pants") - dirtyMap.get("long_pants") > 0)) {
                    // we can recommend long pants as a backup
                    backupPants = "long_pants";
                }
            } else if ((ownedMap.get("long_pants") - dirtyMap.get("long_pants") > 0)) {
                // if not recommend long pants if he owns it
                pantsRecommendation = "long_pants";
            } else {
                // he has no pants...
                pantsRecommendation = "NONE";
            }
            // recommend 
        } else {
            // we prefer long pants
            if (ownedMap.get("long_pants") - dirtyMap.get("long_pants") > 0) {
                pantsRecommendation = "long_pants";
            } else if ((ownedMap.get("shorts") - dirtyMap.get("shorts") > 0)) {
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
            if (ownedMap.get("tank_top") - dirtyMap.get("tank_top") > 0) {
                shirtRecommendation = "tank_top";
                if (ownedMap.get("t_shirt") - dirtyMap.get("t_shirt") > 0) {
                    backupShirt = "t_shirt";
                } else if (ownedMap.get("tank_top") - dirtyMap.get("tank_top") > 0) {
                    backupShirt = "long_sleeve";
                }
            } else if (ownedMap.get("t_shirt") - dirtyMap.get("t_shirt") > 0) {
                // if owned none, then shirt
                shirtRecommendation = "t_shirt";
                if (ownedMap.get("long_sleeve") - dirtyMap.get("long_sleeve") > 0) {
                    backupShirt = "long_sleeve";
                }
            } else if (ownedMap.get("long_sleeve") - dirtyMap.get("long_sleeve") > 0) {
                // if owned none, then long sleeve
                shirtRecommendation = "long_sleeve";
                if (ownedMap.get("t_shirt") - dirtyMap.get("t_shirt") > 0) {
                    backupShirt = "t_shirt";
                }
            }
        } else if (currWeather.getMaxApparentTemp() < highMap.get("long_sleeve")) { // TODO: double check this
            // it is cold enough for a long sleeve
            if (ownedMap.get("long_sleeve") - dirtyMap.get("long_sleeve") > 0) {
                shirtRecommendation = "long_sleeve";
                if (ownedMap.get("t_shirt") - dirtyMap.get("t_shirt") > 0) {
                    backupShirt = "t_shirt";
                }
            } else if (ownedMap.get("t_shirt") - dirtyMap.get("t_shirt") > 0) {
                shirtRecommendation = "t_shirt";
            } else if (ownedMap.get("tank_top") - dirtyMap.get("tank_top") > 0) {
                shirtRecommendation = "tank_top";
            }
        } else {
            // recommend a t-shirt
            if (ownedMap.get("t_shirt") - dirtyMap.get("t_shirt") > 0) {
                shirtRecommendation = "t_shirt";
                if (ownedMap.get("long_sleeve") - dirtyMap.get("long_sleeve") > 0) {
                    backupShirt = "long_sleeve";
                }
            } else if (ownedMap.get("long_sleeve") - dirtyMap.get("long_sleeve") > 0) {
                shirtRecommendation = "long_sleeve";
            } else if (ownedMap.get("tank_top") - dirtyMap.get("tank_top") > 0) {
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
            if (ownedMap.get("winter_coat") - dirtyMap.get("winter_coat") > 0) {
                outwearRecommendation = "winter_coat";
            } else if (ownedMap.get("hoodie") - dirtyMap.get("hoodie") > 0) {
                outwearRecommendation = "hoodie";
            } else if (ownedMap.get("sweater") - dirtyMap.get("sweater") > 0) {
                outwearRecommendation = "sweater";
            } else if (ownedMap.get("windbreaker") - dirtyMap.get("windbreaker") > 0) {
                outwearRecommendation = "windbreaker";
            } else if (ownedMap.get("rain_jacket") - dirtyMap.get("rain_jacket") > 0) {
                outwearRecommendation = "rain_jacket";
            }
        } else if (currWeather.getWindSpeed() > 20) {
            // if windy, recommend windbreaker
            if (ownedMap.get("windbreaker") - dirtyMap.get("windbreaker") > 0) {
                outwearRecommendation = "windbreaker";
            } else if (ownedMap.get("rain_jacket") - dirtyMap.get("rain_jacket") > 0) {
                outwearRecommendation = "rain_jacket";
            } else if (ownedMap.get("hoodie") - dirtyMap.get("hoodie") > 0) {
                outwearRecommendation = "hoodie";
            } else if (ownedMap.get("sweater") - dirtyMap.get("sweater") > 0) {
                outwearRecommendation = "sweater";
            }
        } else if (currWeather.getPrecipType().equals("rain")) {
            // if rainy, recommend rain jacket
            if (ownedMap.get("rain_jacket") - dirtyMap.get("rain_jacket") > 0) {
                outwearRecommendation = "rain_jacket";
            } else if (ownedMap.get("windbreaker") - dirtyMap.get("windbreaker") > 0) {
                outwearRecommendation = "windbreaker";
            } else if (ownedMap.get("hoodie") - dirtyMap.get("hoodie") > 0) {
                outwearRecommendation = "hoodie";
            } else if (ownedMap.get("sweater") - dirtyMap.get("sweater") > 0) {
                outwearRecommendation = "sweater";
            }
        } else if (currWeather.getMaxApparentTemp() < highMap.get("hoodie")) {
            // recommend hoodie/sweater
            if (ownedMap.get("hoodie") - dirtyMap.get("hoodie") > 0) {
                outwearRecommendation = "hoodie";
                if (ownedMap.get("sweater") - dirtyMap.get("sweater") > 0) {
                    backupOuterwear = "sweater";
                }
            } else if (ownedMap.get("sweater") - dirtyMap.get("sweater") > 0) {
                outwearRecommendation = "sweater";
                if (ownedMap.get("hoodie") - dirtyMap.get("hoodie") > 0) {
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
