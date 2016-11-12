package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
// for postgres
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.postgresql.ds.PGPoolingDataSource;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bootstrap {
    public static final String TEST_IP_ADDRESS = "localhost";
    public static final int TEST_PORT = 8080;
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception {
        //Specify the Port at which the server should be run
        int currPort = getHerokuAssignedPort();
        port(currPort);

        try {
            UserService model = new UserService(currPort == 8080);
            new UserController(model);
        } catch (UserService.UserServiceException ex) {
            logger.error("Failed to create a UserService instance. Aborting");
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
