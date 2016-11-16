package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
// for postgres
import java.sql.Connection;
import java.sql.DriverManager;
import org.postgresql.*;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    // to use for testing purposes
    private static String dbHost_test = "ec2-54-243-245-58.compute-1.amazonaws.com";
    private static String dbPort_test = "5432";
    private static String dbName_test = "d6fvfp446bnac1";
    private static String dbUsername_test = "zramgenmiqkrmg";
    private static String dbPassword_test = "2E7ZBZHu1bERfmGuYLzIwJAiWa";

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");

        //Specify the Port at which the server should be run
        int currPort = getHerokuAssignedPort();
        port(currPort);

        try {
            UserService model = new UserService(currPort == 8080);
            new UserController(model);
        } catch (UserService.UserServiceException ex) {
            logger.error("Failed to create a UserService instance. Aborting");
        }

        System.out.println("DONE WITH BOOTSTRAP MAIN");
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 8080; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
