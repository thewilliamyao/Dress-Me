package com.serverapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bootstrap {
    public static final String TEST_IP_ADDRESS = "localhost";
    public static final int TEST_PORT = 8080;
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception {
        //Check if the database file exists in the current directory. Abort if not
        DataSource dataSource = configureDataSource();
        if (dataSource == null) {
            System.out.printf("Could not find server.db in the current directory (%s). Terminating\n",
                    Paths.get(".").toAbsolutePath().normalize());
            System.exit(1);
        }

        //Specify the Port at which the server should be run
        port(getHerokuAssignedPort());

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");

        try {
            UserService model = new UserService(dataSource);
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

    /**
     * Check if the database file exists in the current directory. If it does
     * create a DataSource instance for the file and return it.
     * @return javax.sql.DataSource corresponding to the weather database
     */
    private static DataSource configureDataSource() {
        Path weatherPath = Paths.get(".", "server.db");
        if ( !(Files.exists(weatherPath) )) {
            try { Files.createFile(weatherPath); }
            catch (java.io.IOException ex) {
                logger.error("Failed to create server.db file in current directory. Aborting");
            }
        }

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:server.db");
        return dataSource;

    }
}
