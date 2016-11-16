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

// import javax.sql.DataSource;
// import javax.naming.InitialContext;
// import javax.naming.NamingException;
// import javax.naming.Context;
// import org.postgresql.jdbc3.Jdbc3PoolingDataSource;
// import org.postgresql.jdbc3.Jdbc3ConnectionPool;
// import org.postgresql.ds.PGPoolingDataSource;
// import java.sql.*;
// import org.postgresql.*;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class UserService {

    private Sql2o db;
    private int userCounter = 0; // counter for free value of the id
    private int clothesCounter = 0; // counter for free value of the id
    private int locationCounter = 0; // counter for free value of the id
    private String[] allTops = {"tank_top", "t_shirt", "long_sleeve"}; // types of clothing
    private String[] allPants = {"shorts", "long_pants"}; // types of clothing
    private String[] allOuterwear = {"hoodie", "windbreaker", "sweater", "winter_coat", "rain_jacket"}; // types of clothing
    private String[] allFootwear = {"shoes", "boots", "sandals"}; // types of clothing
    private String[] allAccessories = {"umbrella", "scarf"}; // types of clothing
    private HashMap<String, Double> lowTempMap; // map to hold defaults for high and low temps
    private HashMap<String, Double> highTempMap; // map to hold defaults for high and low temps
    private double TEMP_MAX = 120.0; // limit for temp values
    private double TEMP_MIN = -120.0; // limit for temp values
    private final double BALTIMORE_LATITUDE = 39.330496; // used for default location
    private final double BALTIMORE_LONGITUDE = -76.620046; // used for default location


    private static String dbHost = "ec2-23-23-211-21.compute-1.amazonaws.com";
    private static String dbPort = "5432";
    private static String dbName = "d8gthm1ipiqkps";
    private static String dbUsername = "hhaivykbviqvhs";
    private static String dbPassword = "rWny-OLus9WiTIvQ1k4Q_GVBUV";

    // to use for testing purposes
    private static String dbHost_test = "ec2-54-243-245-58.compute-1.amazonaws.com";
    private static String dbPort_test = "5432";
    private static String dbName_test = "d6fvfp446bnac1";
    private static String dbUsername_test = "zramgenmiqkrmg";
    private static String dbPassword_test = "2E7ZBZHu1bERfmGuYLzIwJAiWa";

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public UserService(boolean localHost) throws UserServiceException {
        // db = new Sql2o("jdbc:postgresql://" + dbHost_test + ":" + dbPort_test + "/" + dbName_test + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername_test, dbPassword_test);

        
        if (!localHost) {
            // db = new Sql2o("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername, dbPassword);
            db = new Sql2o("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername, dbPassword);

            // db = new Sql2o("jdbc:postgres://" + dbHost + ":" + dbPort + "/" + dbName + "?sslmode=requre&user=" + dbUsername + "&password=" + dbPassword); 

                // =true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername, dbPassword);

            // db = new Sql2o("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName, dbUsername, dbPassword);

            // db = new Sql2o("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUsername + "&password=" + dbPassword);
                                // jdbc:postgresql://localhost/test?user=fred&password=secret

            // InitialContext ic = new InitialContext();
            // ic.createSubcontext("java:comp");
            // ic.createSubcontext("java:comp/env");
            // ic.createSubcontext("java:comp/env/jdbc");

            // PGPoolingDataSource source = new PGPoolingDataSource();
            // source.setDataSourceName("A Data Source");
            // source.setServerName(dbHost_test);
            // source.setDatabaseName(dbName_test);
            // source.setUser(dbUsername_test);
            // source.setPassword(dbPassword_test);
            // ic.bind("java:comp/env/jdbc/" + dbName_test, source);
            // DataSource ds = new DataSource();
            // ds.setUrl("jdbc:postgres://" + dbHost + ":" + dbPort + "/" + dbName);
            // ds.setUser(dbUsername);
            // ds.setPassword(dbPassword);
            // ic.bind("java:comp/env/jdbc/" + dbName, ds);
            // db = new Sql2o("jdbc:postgresql://ec2-54-243-245-58.compute-1.amazonaws.com:5432/d6fvfp446bnac1?user=zramgenmiqkrmg&password=2E7ZBZHu1bERfmGuYLzIwJAiWa&sslmode=require");

            // Context webContext = (Context)initContext.lookup("java:/comp/env");
            // db = new sql2o((DataSource) webContext.lookup("jdbc/nameofmyjdbcresource"));
        } else {
            // System.out.println("ON LOCAL HOST");
            db = new Sql2o("jdbc:postgresql://" + dbHost_test + ":" + dbPort_test + "/" + dbName_test + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername_test, dbPassword_test);
            // db = new Sql2o("jdbc:postgres://" + dbHost_test + ":" + dbPort_test + "/" + dbName_test, dbUsername_test, dbPassword_test); 

            // Connection conn = db.open();
            // String dbUrl = System.getenv("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
            // com.dbaccess.BasicDataSource basicDataSource = new com.dbaccess.BasicDataSource();
            // basicDataSource.setUrl(dbUrl);
            // basicDataSource.setUsername(dbUsername);
            // basicDataSource.setPassword(dbPassword);
            // db = new Sql2o(basicDataSource);



            // db = new Sql2o(source);
        }
        
        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sqlUser = "CREATE TABLE IF NOT EXISTS users (user_id INTEGER PRIMARY KEY, " +
                         "                                 email TEXT, password TEXT)" ;
            conn.createQuery(sqlUser).executeUpdate();

            String sqlClothes = "CREATE TABLE IF NOT EXISTS clothes (clothes_id INTEGER PRIMARY KEY, " +
                         "                                 user_id INTEGER, type TEXT, specific_type TEXT, number_owned INTEGER, " + 
                         "                                 number_dirty INTEGER, temp_high DECIMAL, temp_low DECIMAL)" ;
            conn.createQuery(sqlClothes).executeUpdate();

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
            // TODO: turn this into a static DB maybe?
            lowTempMap = new HashMap<String, Double>();
            highTempMap = new HashMap<String, Double>();
            // shirts
            lowTempMap.put("tank_top", new Double(80));
            highTempMap.put("tank_top", new Double(TEMP_MAX));
            lowTempMap.put("t_shirt", new Double(TEMP_MIN));
            highTempMap.put("t_shirt", new Double(TEMP_MAX));
            lowTempMap.put("long_sleeve", new Double(TEMP_MIN));
            highTempMap.put("long_sleeve", new Double(80));
            // pants
            lowTempMap.put("shorts", new Double(60));
            highTempMap.put("shorts", new Double(120));
            lowTempMap.put("long_pants", new Double(TEMP_MIN));
            highTempMap.put("long_pants", new Double(80));
            // outerwear
            lowTempMap.put("hoodie", new Double(50));
            highTempMap.put("hoodie", new Double(80));
            lowTempMap.put("windbreaker", new Double(50));
            highTempMap.put("windbreaker", new Double(TEMP_MAX));
            lowTempMap.put("sweater", new Double(50));
            highTempMap.put("sweater", new Double(80));
            lowTempMap.put("winter_coat", new Double(TEMP_MIN));
            highTempMap.put("winter_coat", new Double(40));
            // accessories
            lowTempMap.put("umbrella", new Double(TEMP_MIN));
            highTempMap.put("umbrella", new Double(TEMP_MAX));
            lowTempMap.put("scarf", new Double(TEMP_MIN));
            highTempMap.put("scarf", new Double(50));
            // shoes
            lowTempMap.put("boots", new Double(TEMP_MIN));
            highTempMap.put("boots", new Double(40));
            lowTempMap.put("sandals", new Double(80));
            highTempMap.put("sandals", new Double(TEMP_MAX));
            lowTempMap.put("shoes", new Double(TEMP_MIN));
            highTempMap.put("shoes", new Double(TEMP_MAX));
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
                        .addParameter("type", type)
                        .addParameter("specificType", s)
                        .addParameter("numberOwned", 10) //TODO change later to 0
                        .addParameter("numberDirty", 0)
                        .addParameter("tempHigh", highTempMap.containsKey(s) ? highTempMap.get(s) : TEMP_MAX)
                        .addParameter("tempLow", lowTempMap.containsKey(s) ? lowTempMap.get(s) : TEMP_MIN)
                    .executeUpdate();
            } catch (Sql2oException ex) {
                logger.error("UserService.addClothing: Failed to add new clothing entry", ex);
                throw new UserServiceException("UserService.addClothing: Failed to add new clothing entry", ex);
            }
        }
    }

    public User createNewUser(String body) throws UserServiceException {
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
        addClothing("top", allTops, currUserId);
        addClothing("pants", allPants, currUserId);
        addClothing("outerwear", allOuterwear, currUserId);
        addClothing("footwear", allFootwear, currUserId);
        addClothing("accessory", allAccessories, currUserId);

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

    public List<Clothes> getClothesList(int id) throws UserServiceException {
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
                    .addParameter("userId", id)
                    .executeAndFetch(Clothes.class);
            return allClothes;
        } catch (Sql2oException ex) {
            logger.error("UserService.getClothesMap: Failed to get clothes map", ex);
            throw new UserServiceException("UserService.getClothesMap: Failed to get clothes map", ex);
        }
    }

    // returns a map of item : number_owned
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

    public HashMap<String, Recommendation> getRecommendation(int currId) throws UserServiceException {
        // get the location lat and long
        Location currLocation = getLocation(currId);

        // get the weather based on the location
        Weather currWeather = new Weather(currLocation.getLatitude(), currLocation.getLongitude());

        // recreate clothes map for this user
        List<Clothes> currClothes = getClothesList(currId);
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

    public boolean markDirty(int currId, String body) throws UserServiceException {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(body).getAsJsonObject();
        String top = obj.get("top").getAsString();
        String pants = obj.get("pants").getAsString();
        String footwear = obj.get("footwear").getAsString();
        String accessory = obj.get("accessory").getAsString();
        String outerwear = obj.get("outerwear").getAsString();

        // obtain the current count of dirty items
        String sqlTops = "SELECT * FROM clothes WHERE (user_id = :userId AND type = :type)";
        HashMap<String, Integer> dirtyMap = new HashMap<String, Integer>();
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
                    .addParameter("userId", currId)
                    .addParameter("type", "top")
                    .executeAndFetch(Clothes.class);
            for (Clothes c : allClothes) {
                dirtyMap.put(c.getSpecificType(), c.getNumberDirty());
            }

        } catch (Sql2oException ex) {
            logger.error("UserService.markDirty: Failed to get clothes map", ex);
            throw new UserServiceException("UserService.markDirty: Failed to get clothes map", ex);
        }

        // update in the local map
        dirtyMap.put(top, (dirtyMap.get(top) + 1)); 
        // update the top we need to
        String updateDirty = "UPDATE clothes SET number_dirty = :numberDirty WHERE (user_id = :userId AND specific_type = :specificType)";

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
                .addParameter("specificType", top)
                .addParameter("numberDirty", dirtyMap.get(top))
                .addParameter("userId", currId)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("UserService.markDirty: Failed to update clothes", ex);
            throw new UserServiceException("UserService.markDirty: Failed to update clothes", ex);
        }

        // then check for each item, if there is less than 30% clean, return true, to signal should do laundry
        HashMap<String, Integer> ownedMap = getClothesMap(currId);
        for (HashMap.Entry<String, Integer> entry : dirtyMap.entrySet()) {
            // if we have more than 70% dirty, then we return true
            if (entry.getValue() > (0.7) * ownedMap.get(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    public void markClean(int currId) throws UserServiceException {
        // set all dirty fields to be 0
        String updateDirty = "UPDATE clothes SET number_dirty = :numberDirty WHERE user_id = :userId";
        
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
                .addParameter("numberDirty", 0)
                .addParameter("userId", currId)
                .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("UserService.markClean: Failed to update clean clothes", ex);
            throw new UserServiceException("UserService.markClean: Failed to update clean clothes", ex);
        }
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
