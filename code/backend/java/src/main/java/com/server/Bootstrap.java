package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
// for postgres
// import java.sql.Connection;
// import java.sql.DriverManager;
import org.postgresql.*;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private static final String dbHost = "ec2-23-23-211-21.compute-1.amazonaws.com";
    private static final String dbPort = "5432";
    private static final String dbName = "d8gthm1ipiqkps";
    private static final String dbUsername = "hhaivykbviqvhs";
    private static final String dbPassword = "rWny-OLus9WiTIvQ1k4Q_GVBUV";

    // to use for testing purposes
    private static final String dbHost_test = "ec2-54-243-245-58.compute-1.amazonaws.com";
    private static final String dbPort_test = "5432";
    private static final String dbName_test = "d6fvfp446bnac1";
    private static final String dbUsername_test = "zramgenmiqkrmg";
    private static final String dbPassword_test = "2E7ZBZHu1bERfmGuYLzIwJAiWa";

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");

        //Specify the Port at which the server should be run
        int currPort = getHerokuAssignedPort();
        port(currPort);

        // connect to the appropriate db, either the test or regular
        Sql2o db;
        boolean localHost = (currPort == 8080) ? true : false;

        if (!localHost) {
            db = new Sql2o("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername, dbPassword);
        } else {
            db = new Sql2o("jdbc:postgresql://" + dbHost_test + ":" + dbPort_test + "/" + dbName_test + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername_test, dbPassword_test);
        }

        // create all services and controllers
        try {
            UserService userModel = new UserService(db);
            new UserController(userModel);
        } catch (UserService.UserServiceException ex) {
            logger.error("Failed to create a UserService instance. Aborting");
        }
        try {
            ClothesService clothesModel = new ClothesService(db);
            new ClothesController(clothesModel);
        } catch (ClothesService.ClothesServiceException ex) {
            logger.error("Failed to create a ClothesService instance. Aborting");
        }
        try {
            LocationService locationModel = new LocationService(db);
            new LocationController(locationModel);
        } catch (LocationService.LocationServiceException ex) {
            logger.error("Failed to create a LocationService instance. Aborting");
        }
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 8080; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
