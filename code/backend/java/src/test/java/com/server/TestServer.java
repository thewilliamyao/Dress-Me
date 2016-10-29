package com.serverapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sqlite.SQLiteDataSource;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TestServer {

    //------------------------------------------------------------------------//
    // Setup
    //------------------------------------------------------------------------//

    @Before
    public void setup() throws Exception {
        //Clear the database and then start the server
        clearDB();

        //Start the main server
        Bootstrap.main(null);
        Spark.awaitInitialization();
    }

    @After
    public void tearDown() {
        //Stop the server
        clearDB();
        Spark.stop();
    }

    //------------------------------------------------------------------------//
    // Tests for User
    //------------------------------------------------------------------------//
    @Test
    public void testAddUser() throws Exception {
        UserJson firstUser = new UserJson("test", "testpassword");
        Response radd = request("POST", "/api/v1/user", firstUser);
        User expectedFirstUser = new User(0, "test", "testpassword");
        String expectedJson = new Gson().toJson(expectedFirstUser);
        assertEquals(201, radd.httpStatus);
        assertEquals(expectedJson, radd.content);
    }

    @Test
    public void testDefaultCloset() throws Exception {
        // create new user
        // UserJson firstUser = new UserJson("test", "testpassword");
        // Response radd = request("POST", "/api/v1/user", firstUser);
        testAddUser();
        // check closet
        ClosetJson expectedCloset = new ClosetJson();
        String expectedJson = new Gson().toJson(expectedCloset);
        Response radd = request("GET", "/api/v1/user/closet/0", null);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedJson, radd.content);
    }

    @Test
    public void testUpdateClosetItem() throws Exception {
        // create new user
        // UserJson firstUser = new UserJson("test", "testpassword");
        // Response radd = request("POST", "/api/v1/user", firstUser);
        testAddUser();
        ClosetJson expectedCloset = new ClosetJson();
        // update a few closet items
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("long_pants", 10);
        expectedCloset.long_pants = 10;
        String expectedJson = new Gson().toJson(expectedCloset);
        Response radd = request("PUT", "/api/v1/user/closet/0", closetUpdate);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedJson, radd.content);
        // another update
        closetUpdate.type = "t_shirt";
        closetUpdate.number = 15;
        expectedCloset.t_shirt = 15;
        expectedJson = new Gson().toJson(expectedCloset);
        radd = request("PUT", "/api/v1/user/closet/0", closetUpdate);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedJson, radd.content);
    }

    @Test
    public void testUpdateLocation() throws Exception {
        // create new user
        testAddUser();

        // change location
        LocationUpdateJson locationUpdate = new LocationUpdateJson(21.2718, -157.7738);
        Response radd = request("PUT", "/api/v1/user/location/0", locationUpdate);
        Location expectedLocation = new Location(0, 0, 21.2718, -157.7738);
        String expectedJson = new Gson().toJson(expectedLocation);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedJson, radd.content);
    }

    //------------------------------------------------------------------------//
    // Generic Helper Methods and classes
    //------------------------------------------------------------------------//
    
    private Response request(String method, String path, Object content) {
        try {
			URL url = new URL("http", Bootstrap.TEST_IP_ADDRESS, Bootstrap.TEST_PORT, path);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoInput(true);
            if (content != null) {
                String contentAsJson = new Gson().toJson(content);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter output = new OutputStreamWriter(http.getOutputStream());
                output.write(contentAsJson);
                output.flush();
                output.close();
            }

            String responseBody = IOUtils.toString(http.getInputStream());
			return new Response(http.getResponseCode(), responseBody);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}
    }

    private static class UserJson {
        public String email;
        public String password;
        public UserJson(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    private static class ClosetJson {
        public int umbrella;
        public int long_pants;
        public int scarf;
        public int long_sleeve;
        public int tank_top;
        public int sandals;
        public int shoes;
        public int hoodie;
        public int windbreaker;
        public int boots;
        public int sweater;
        public int shorts;
        public int winter_coat;
        public int t_shirt;

        public ClosetJson() {
            this.umbrella = 0;
            this.long_pants = 0;
            this.scarf = 0;
            this.long_sleeve = 0;
            this.tank_top = 0;
            this.sandals = 0;
            this.shoes = 0;
            this.hoodie = 0;
            this.windbreaker = 0;
            this.boots = 0;
            this.sweater = 0;
            this.shorts = 0;
            this.winter_coat = 0;
            this.t_shirt = 0;
        }
    }

    private static class ClosetUpdateJson {
        String type;
        int number;
        public ClosetUpdateJson(String type, int number) {
            this.type = type;
            this.number = number;
        }
    }

    private static class LocationUpdateJson {
        double latitude;
        double longitude;
        public LocationUpdateJson(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private static class Response {

		public String content;
        
		public int httpStatus;

		public Response(int httpStatus, String content) {
			this.content = content;
            this.httpStatus = httpStatus;
		}

        public <T> T getContentAsObject(Type type) {
            return new Gson().fromJson(content, type);
        }
	}

    //------------------------------------------------------------------------//
    // TodoApp Specific Helper Methods and classes
    //------------------------------------------------------------------------//

    private void clearDB() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:server.db");

        Sql2o db = new Sql2o(dataSource);

        try (Connection conn = db.open()) {
            String sql = "DROP TABLE IF EXISTS users";
            conn.createQuery(sql).executeUpdate();
            sql = "DROP TABLE IF EXISTS clothes";
            conn.createQuery(sql).executeUpdate();
            sql = "DROP TABLE IF EXISTS locations";
            conn.createQuery(sql).executeUpdate();
        }
    }
}