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
        //Check if the database file exists in the current directory. Abort if not
        // DataSource dataSource = configureDataSource();
        // if (dataSource == null) {
        //     System.out.printf("Could not find server.db in the current directory (%s). Terminating\n",
        //             Paths.get(".").toAbsolutePath().normalize());
        //     System.exit(1);
        // }

        // connect to heroku postgres db
        // DataSource dataSource = postgresDataSource();
        // try {
        //     postgresDataSource();
        // } catch(Exception ex) {
        //     ex.printStackTrace();
        // }

        //Specify the Port at which the server should be run
        port(getHerokuAssignedPort());

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");

        try {
            UserService model = new UserService(null);
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

    private static DataSource postgresDataSource() throws SQLException {

        // String connectionString ="postgres://hhaivykbviqvhs:rWny-OLus9WiTIvQ1k4Q_GVBUV@ec2-23-23-211-21.compute-1.amazonaws.com:5432/d8gthm1ipiqkps";
        // String username = "****";
        // String password = "***";
        // return DriverManager.getConnection(connectionString, username, password);

        // try {
        //     String url = "jdbc:postgresql://ec2-23-23-211-21.compute-1.amazonaws.com:5432/d8gthm1ipiqkps";
        //     Properties props = new Properties();
        //     props.setProperty("user", "hhaivykbviqvhs");
        //     props.setProperty("password", "rWny-OLus9WiTIvQ1k4Q_GVBUV");
        //     props.setProperty("ssl", "true");
        //     return DriverManager.getConnection(url, props);
        // } catch (SQLException e) {
        //     System.out.println("Connection Failed! Check output console");
        //     e.printStackTrace();
        //     return null;
        // }
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("Server Data Source");
        source.setServerName("ec2-23-23-211-21.compute-1.amazonaws.com");
        source.setDatabaseName("d8gthm1ipiqkps");
        source.setUser("hhaivykbviqvhs");
        source.setPassword("rWny-OLus9WiTIvQ1k4Q_GVBUV");
        source.setMaxConnections(10);
        return source;
    }
    /**
     * Check if the database file exists in the current directory. If it does
     * create a DataSource instance for the file and return it.
     * @return javax.sql.DataSource corresponding to the database
     */
    private static DataSource configureDataSource() {
        Path localPath = Paths.get(".", "server.db");
        if ( !(Files.exists(localPath) )) {
            try { Files.createFile(localPath); }
            catch (java.io.IOException ex) {
                logger.error("Failed to create server.db file in current directory. Aborting");
            }
        }

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:server.db");
        return dataSource;

    }
}
