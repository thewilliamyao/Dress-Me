package com.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.server.Bootstrap;
import com.server.Location;
import com.server.User;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
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

    private static final int NUMBER_CLOTHES_DEFAULT = 0;
    private static final String API_PREFIX = "/api/v1/";
    // to use for testing purposes
    private static String dbHost_test = "ec2-54-243-245-58.compute-1.amazonaws.com";
    private static String dbPort_test = "5432";
    private static String dbName_test = "d6fvfp446bnac1";
    private static String dbUsername_test = "zramgenmiqkrmg";
    private static String dbPassword_test = "2E7ZBZHu1bERfmGuYLzIwJAiWa";
    private String currToken = "";

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
        Response radd = request("POST", API_PREFIX + "user", firstUser);
        LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
        assertEquals(201, radd.httpStatus);
        assertEquals(token.getId(), 0);
        assertNotEquals(token.getToken(), "");
        currToken = token.getToken();
    }

    @Test
    public void testLogin() throws Exception {
        testAddUser();
        UserJson firstUser = new UserJson("test", "testpassword");
        Response radd = request("PUT", API_PREFIX + "login", firstUser);
        LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(token.getId(), 0);
        assertNotEquals(token.getToken(), "");
        currToken = token.getToken();
    }

    @Test
    public void testInvalidToken() throws Exception {
        testLogin();
        currToken = "ASDHJFA";
        Response radd = request("GET", API_PREFIX + "closet/0", null);
        assertEquals(403, radd.httpStatus);
    }

    @Test
    public void testLoginWrongPasswordFail() throws Exception {
        testAddUser();
        UserJson firstUser = new UserJson("test", "wrongpassword");
        Response radd = request("PUT", API_PREFIX + "login", firstUser);
        LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
        assertEquals(403, radd.httpStatus);
        assertEquals(-1, token.getId());
    }

    @Test
    public void testLoginWrongUsernameFail() throws Exception {
        testAddUser();
        UserJson firstUser = new UserJson("other", "testpassword");
        Response radd = request("PUT", API_PREFIX + "login", firstUser);
        LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
        assertEquals(403, radd.httpStatus);
        assertEquals(-1, token.getId());
    }

    @Test
    public void testLoginNoUsernameFail() throws Exception {
        testAddUser();
        UserJson firstUser = new UserJson("", "testpassword");
        Response radd = request("PUT", API_PREFIX + "login", firstUser);
        LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
        assertEquals(403, radd.httpStatus);
        assertEquals(-1, token.getId());
    }
    
    @Test
    public void testLoginNoPasswordFail() throws Exception {
        testAddUser();
        UserJson firstUser = new UserJson("other", "");
        Response radd = request("PUT", API_PREFIX + "login", firstUser);
        LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
        assertEquals(403, radd.httpStatus);
        assertEquals(-1, token.getId());
    }

    @Test
    public void testCreateMultipleUsers() throws Exception {
        for (int i = 0; i < 10; i++) {
            UserJson firstUser = new UserJson("test" + Integer.toString(i), "testpassword");
            Response radd = request("POST", API_PREFIX + "user", firstUser);
            LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
            assertEquals(201, radd.httpStatus);
            assertEquals(token.getId(), i);
            assertNotEquals(token.getToken(), "");
        }
    }

    @Test
    public void testCreateUserNoEmailFail() throws Exception {
        UserJson emptyUser = new UserJson("", "test");
        Response badd = request("POST", API_PREFIX + "user", emptyUser);
        LoginToken token = new Gson().fromJson(badd.content, LoginToken.class);
        assertEquals(403, badd.httpStatus);
        assertEquals(-1, token.getId());
    }

    @Test
    public void testCreateUserNoPasswordFail() throws Exception {
        UserJson emptyPassword = new UserJson("test", "");
        Response badd = request("POST", API_PREFIX + "user", emptyPassword);
        LoginToken token = new Gson().fromJson(badd.content, LoginToken.class);
        assertEquals(403, badd.httpStatus);
        assertEquals(-1, token.getId());
    }

    @Test
    public void testCreateSameUserFail() throws Exception {
        UserJson first = new UserJson("first", "password");
        Response radd = request("POST", API_PREFIX + "user", first);
        assertEquals(201, radd.httpStatus);
        radd = request("POST", API_PREFIX + "user", first);
        LoginToken token = new Gson().fromJson(radd.content, LoginToken.class);
        assertEquals(403, radd.httpStatus);
        assertEquals(-1, token.getId());
    }

    //------------------------------------------------------------------------//
    // Tests for Closet
    //------------------------------------------------------------------------//
    @Test
    public void testDefaultCloset() throws Exception {
        // create new user
        testAddUser();
        // check closet
        ClosetJson expectedCloset = new ClosetJson();
        String expectedJson = new Gson().toJson(expectedCloset);
        Response radd = request("GET", API_PREFIX + "closet/0", null);
        ClosetJson resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
    }

    @Test
    public void testUpdateClosetItem() throws Exception {
        // create new user
        testLogin();
        ClosetJson expectedCloset = new ClosetJson();
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("t_shirt", 5);
        expectedCloset.t_shirt = 5;
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        ClosetJson resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);

        closetUpdate = new ClosetUpdateJson("long_pants", 7);
        expectedCloset.long_pants = 7;
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
    }
    public void testInvalidClosetUpdate() throws Exception {
        // create new User
        testLogin();
        // grab initial closet
        ClosetJson expectedCloset = new ClosetJson();
        String expectedJson = new Gson().toJson(expectedCloset);
        Response radd = request("GET", API_PREFIX + "closet/0", null);
        ClosetJson resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
        // now update something invalid
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("t_shirt", -3);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        assertEquals(400, radd.httpStatus);
        // check that closet is not any different now
        radd = request("GET", API_PREFIX + "closet/0", null);
        resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
    }


    @Test
    public void testGetClosetNoTokenFail() throws Exception {
        // create new user
        testAddUser();
        // set empty token and send request
        currToken = "";
        Response radd = request("GET", API_PREFIX + "closet/0", null);
        assertEquals(403, radd.httpStatus);
    }

    @Test
    public void testUpdateClosetNoTokenFail() throws Exception {
        // create new user
        testLogin();
        currToken = "";
        // update a few closet items
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("long_pants", 6);
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        assertEquals(403, radd.httpStatus);
    }

    //------------------------------------------------------------------------//
    // Tests for Location
    //------------------------------------------------------------------------//
    @Test
    public void testUpdateLocation() throws Exception {
        // create new user
        testLogin();
        // change location
        LocationUpdateJson locationUpdate = new LocationUpdateJson(21.2718, -157.7738);
        Response radd = request("PUT", API_PREFIX + "location/0", locationUpdate);
        Location expectedLocation = new Location(0, 0, 21.2718, -157.7738);
        Location resultLocation = new Gson().fromJson(radd.content, Location.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedLocation, resultLocation);
    }

    @Test
    public void testUpdateLocationNoTokenFail() throws Exception {
        // create new user
        testLogin();
        currToken = "ASDOFUJASDH";
        // change location
        LocationUpdateJson locationUpdate = new LocationUpdateJson(21.2718, -157.7738);
        Response radd = request("PUT", API_PREFIX + "location/0", locationUpdate);
        assertEquals(403, radd.httpStatus);
    }

    //------------------------------------------------------------------------//
    // Tests for Laundry
    //------------------------------------------------------------------------//
    @Test
    public void testDefaultLaundry() throws Exception {
        // create new user
        testAddUser();
        // check closet
        ClosetJson expectedCloset = new ClosetJson();
        expectedCloset.setToZero();
        String expectedJson = new Gson().toJson(expectedCloset);
        Response radd = request("GET", API_PREFIX + "laundry/0", null);
        ClosetJson resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
    }

    @Test
    public void testMarkShirtsDirty() throws Exception {
        // create a new user
        testLogin();
        // mark an item as dirty
        DirtyClothesJson dirty = new DirtyClothesJson("t_shirt", "NONE", "NONE", "NONE", "NONE");

        // make it so we own 10 t_shirts
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("t_shirt", 10);
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("long_pants", 10);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);

        // mark a shirt dirty until we expect it to return true
        for (int i = 0; i < 0.7*10; i++) {
            radd = request("PUT", API_PREFIX + "dirty/0", dirty);
            assertEquals(200, radd.httpStatus);
            assertEquals("false", radd.content);
        }

        // one more time, should return true this time
        radd = request("PUT", API_PREFIX + "dirty/0", dirty);
        assertEquals(200, radd.httpStatus);
        assertEquals("true", radd.content);
    }

    @Test
    public void testMarkPantsDirty() throws Exception {
        // create a new user
        testLogin();
        // mark an item as dirty
        DirtyClothesJson dirty = new DirtyClothesJson("NONE", "long_pants", "NONE", "NONE", "NONE");

        // make it so we own 10 long_pants
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("long_pants", 10);
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);

        // mark pants dirty until we expect it to return true
        for (int i = 0; i < 0.7*10; i++) {
            for (int j = 0; j < 3; j++) {
                radd = request("PUT", API_PREFIX + "dirty/0", dirty);
                assertEquals(200, radd.httpStatus);
                assertEquals("false", radd.content);
            }
        }

        // one more time, should return true this time
        for (int j = 0; j < 2; j++) {
            radd = request("PUT", API_PREFIX + "dirty/0", dirty);
            assertEquals(200, radd.httpStatus);
            assertEquals("false", radd.content);
        }
        radd = request("PUT", API_PREFIX + "dirty/0", dirty);
        assertEquals(200, radd.httpStatus);
        assertEquals("true", radd.content);
    }

    @Test
    public void testMarkOtherDirty() throws Exception {
        // create a new user
        testLogin();
        // mark an item as dirty
        DirtyClothesJson dirty = new DirtyClothesJson("NONE", "NONE", "hoodie", "shoes", "umbrella");

        // make it so we own 1 of these items
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("hoodie", 1);
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("shoes", 1);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("umbrella", 1);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);

        // These items should never return dirty.
        for (int i = 0; i < 25; i++) {
            radd = request("PUT", API_PREFIX + "dirty/0", dirty);
            assertEquals(200, radd.httpStatus);
            assertEquals("false", radd.content);
        }
    }

    @Test
    public void testMarkDirtyNoTokenFail() throws Exception {
        // create a new user
        testLogin();
        currToken = "";
        // mark an item as dirty
        DirtyClothesJson dirty = new DirtyClothesJson("t_shirt", "long_pants", "shoes", "NONE", "NONE");

        // mark an item dirty until we expect it to return true
        Response radd = request("PUT", API_PREFIX + "dirty/0", dirty);
        assertEquals(403, radd.httpStatus);
    }

    @Test
    public void testMoreDirty() throws Exception {
        // other stuff is already past the limit
        testMarkShirtsDirty();

        // update number owned for the items we are testing
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("long_sleeve", 10);
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("long_pants", 10);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("shoes", 10);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("tank_top", 10);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);

        // mark items as dirty, should all continue to return true
        DirtyClothesJson dirty = new DirtyClothesJson("long_sleeve", "long_pants", "shoes", "NONE", "NONE");
        radd = request("PUT", API_PREFIX + "dirty/0", dirty);
        assertEquals(200, radd.httpStatus);
        assertEquals("true", radd.content);

        dirty.top = "tank_top";
        radd = request("PUT", API_PREFIX + "dirty/0", dirty);
        assertEquals(200, radd.httpStatus);
        assertEquals("true", radd.content);

        dirty.top = "tank_top";
        radd = request("PUT", API_PREFIX + "dirty/0", dirty);
        assertEquals(200, radd.httpStatus);
        assertEquals("true", radd.content);
    }

    @Test
    public void testMarkClean() throws Exception {
        testMarkShirtsDirty();
        // now lets mark clean
        Response radd = request("PUT", API_PREFIX + "clean/0", null);
        assertEquals(200, radd.httpStatus);
        // now mark dirty should work again
        DirtyClothesJson dirty = new DirtyClothesJson("t_shirt", "long_pants", "shoes", "NONE", "NONE");

        // mark an item dirty until we expect it to return true
        for (int i = 0; i < 0.7*10; i++) {
            radd = request("PUT", API_PREFIX + "dirty/0", dirty);
            assertEquals(200, radd.httpStatus);
            assertEquals("false", radd.content);
        }

        // one more time, should return true this time
        radd = request("PUT", API_PREFIX + "dirty/0", dirty);
        assertEquals(200, radd.httpStatus);
        assertEquals("true", radd.content);
    }

    @Test
    public void testMarkCleanNoTokenFail() throws Exception {
        testLogin();
        currToken = "ASDFASD";
        // now lets mark clean
        Response radd = request("PUT", API_PREFIX + "clean/0", null);
        assertEquals(403, radd.httpStatus);
    }

    @Test
    public void testGetLaundry() throws Exception {
        // create a new user
        testLogin();
        // mark an item as dirty
        DirtyClothesJson dirty = new DirtyClothesJson("t_shirt", "long_pants", "NONE", "NONE", "NONE");

        ClosetJson expectedLaundry = new ClosetJson();
        expectedLaundry.setToZero();
        ClosetJson actualLaundry;
        // mark an item dirty and ensure that the counts are working as expected
        Response radd;
        for (int i = 0; i < 0.7*NUMBER_CLOTHES_DEFAULT; i++) {
            radd = request("PUT", API_PREFIX + "dirty/0", dirty);
            radd = request("GET", API_PREFIX + "laundry/0", null);
            expectedLaundry.t_shirt += 1;
            if ((i+1)%3 == 0) {
                expectedLaundry.long_pants += 1;
            }
            actualLaundry = new Gson().fromJson(radd.content, ClosetJson.class);
            assertEquals(200, radd.httpStatus);
            assertEquals(expectedLaundry, actualLaundry);
        }
    }

    @Test
    public void testGetLaundryNoTokenFail() throws Exception {
        testLogin();
        currToken = "";
        Response radd = request("GET", API_PREFIX + "laundry/0", null);
        assertEquals(403, radd.httpStatus);
    }

    @Test
    public void testUpdateLaundry() throws Exception {
        // create new user
        testLogin();
        ClosetJson expectedCloset = new ClosetJson();
        expectedCloset.setToZero();
        // set number owned to be higher than those values so we can test updates
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("t_shirt", 10);
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("long_pants", 10);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);

        // now do an update
        closetUpdate = new ClosetUpdateJson("t_shirt", 5);
        expectedCloset.t_shirt = 5;
        radd = request("PUT", API_PREFIX + "laundry/0", closetUpdate);
        ClosetJson resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);

        // try another update
        closetUpdate = new ClosetUpdateJson("long_pants", 5);
        expectedCloset.long_pants = 5;
        radd = request("PUT", API_PREFIX + "laundry/0", closetUpdate);
        resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
    }

    @Test
    public void testInvalidLaundryUpdate() throws Exception {
        // create new user
        testLogin();
        // grab inital laundry
        ClosetJson expectedCloset = new ClosetJson();
        String expectedJson = new Gson().toJson(expectedCloset);
        Response radd = request("GET", API_PREFIX + "laundry/0", null);
        ClosetJson resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
        // now do an invalid update because negative
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("t_shirt", -5);
        radd = request("PUT", API_PREFIX + "laundry/0", closetUpdate);
        assertEquals(400, radd.httpStatus);
        // now check laundry to make sure its the same
        radd = request("GET", API_PREFIX + "laundry/0", null);
        resultCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, resultCloset);
        // try an invalid update because its more than the number owned
        // by default number owned is 0 so....
        closetUpdate = new ClosetUpdateJson("t_shirt", 5);
        radd = request("PUT", API_PREFIX + "laundry/0", closetUpdate);
        assertEquals(400, radd.httpStatus);
    }

    @Test
    public void testChangeNumberOwnedLessThanNumberDirty() throws Exception {
        //create user
        testLogin();
        // set some values up
        ClosetUpdateJson closetUpdate = new ClosetUpdateJson("t_shirt", 5);
        Response radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        closetUpdate = new ClosetUpdateJson("t_shirt", 4);
        radd = request("PUT", API_PREFIX + "laundry/0", closetUpdate);
        // now set number owned to be less than that
        closetUpdate = new ClosetUpdateJson("t_shirt", 3);
        radd = request("PUT", API_PREFIX + "closet/0", closetUpdate);
        ClosetJson expectedCloset = new ClosetJson();
        expectedCloset.t_shirt = 3;
        ClosetJson actualCloset = new Gson().fromJson(radd.content, ClosetJson.class);
        assertEquals(200, radd.httpStatus);
        assertEquals(expectedCloset, actualCloset);
    }
    //------------------------------------------------------------------------//
    // Tests for Recommendation
    //------------------------------------------------------------------------//

    // TODO

    //------------------------------------------------------------------------//
    // Generic Helper Methods and classes
    //------------------------------------------------------------------------//
    private Response request(String method, String path, Object content) throws Exception {
        String responseBody = "";
        URL url = new URL("http", "localhost", 8080, path);
        // URL url = new URL("http", "https://dry-beyond-51182.herokuapp.com", path);

        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        try {
            http.setRequestMethod(method);
            http.setDoInput(true);
            http.setRequestProperty("token", currToken);
            if (content != null) {
                String contentAsJson = new Gson().toJson(content);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter output = new OutputStreamWriter(http.getOutputStream());
                output.write(contentAsJson);
                output.flush();
                output.close();
            }
            responseBody = IOUtils.toString(http.getInputStream());
        } catch (IOException e) {
            if (responseBody == "") {
                responseBody = IOUtils.toString(http.getErrorStream());
            }
		}
        return new Response(http.getResponseCode(), responseBody);

    }

    private static class UserJson {
        public String email;
        public String password;
        public UserJson(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    private static class DirtyClothesJson {
        public String top;
        public String pants;
        public String footwear;
        public String accessory;
        public String outerwear;

        public DirtyClothesJson(String top, String pants, String footwear, String accessory, String outerwear) {
            this.top = top;
            this.pants = pants;
            this.footwear = footwear;
            this.accessory = accessory;
            this.outerwear = outerwear;
        }
    }
    
    private static class ClosetJson {
        public int umbrella;
        public int long_pants;
        public int scarf;
        public int long_sleeve;
        public int tank_top;
        public int sandals;
        public int rain_jacket;
        public int shoes;
        public int hoodie;
        public int windbreaker;
        public int boots;
        public int sweater;
        public int shorts;
        public int winter_coat;
        public int t_shirt;

        public ClosetJson() {
            this.umbrella       = NUMBER_CLOTHES_DEFAULT;
            this.long_pants     = NUMBER_CLOTHES_DEFAULT;
            this.scarf          = NUMBER_CLOTHES_DEFAULT;
            this.long_sleeve    = NUMBER_CLOTHES_DEFAULT;
            this.tank_top       = NUMBER_CLOTHES_DEFAULT;
            this.sandals        = NUMBER_CLOTHES_DEFAULT;
            this.rain_jacket    = NUMBER_CLOTHES_DEFAULT;
            this.shoes          = NUMBER_CLOTHES_DEFAULT;
            this.hoodie         = NUMBER_CLOTHES_DEFAULT;
            this.windbreaker    = NUMBER_CLOTHES_DEFAULT;
            this.boots          = NUMBER_CLOTHES_DEFAULT;
            this.sweater        = NUMBER_CLOTHES_DEFAULT;
            this.shorts         = NUMBER_CLOTHES_DEFAULT;
            this.winter_coat    = NUMBER_CLOTHES_DEFAULT;
            this.t_shirt        = NUMBER_CLOTHES_DEFAULT;
        }

        public void setToZero() {
            this.umbrella       = 0;
            this.long_pants     = 0;
            this.scarf          = 0;
            this.long_sleeve    = 0;
            this.tank_top       = 0;
            this.sandals        = 0;
            this.rain_jacket    = 0;
            this.shoes          = 0;
            this.hoodie         = 0;
            this.windbreaker    = 0;
            this.boots          = 0;
            this.sweater        = 0;
            this.shorts         = 0;
            this.winter_coat    = 0;
            this.t_shirt        = 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!ClosetJson.class.isAssignableFrom(obj.getClass())) {
                return false;
            }
            final ClosetJson other = (ClosetJson) obj;
            if (this.umbrella != other.umbrella) {
                return false;
            }
            if (this.long_pants != other.long_pants) {
                return false;
            }
            if (this.scarf != other.scarf) {
                return false;
            }
            if (this.long_sleeve != other.long_sleeve) {
                return false;
            }
            if (this.tank_top != other.tank_top) {
                return false;
            }
            if (this.sandals != other.sandals) {
                return false;
            }
            if (this.rain_jacket != other.rain_jacket) {
                return false;
            }
            if (this.shoes != other.shoes) {
                return false;
            }
            if (this.hoodie != other.hoodie) {
                return false;
            }
            if (this.windbreaker != other.windbreaker) {
                return false;
            }
            if (this.boots != other.boots) {
                return false;
            }
            if (this.sweater != other.sweater) {
                return false;
            }
            if (this.shorts != other.shorts) {
                return false;
            }
            if (this.winter_coat != other.winter_coat) {
                return false;
            }
            if (this.t_shirt != other.t_shirt) {
                return false;
            }
            return true;
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
        Sql2o db = new Sql2o("jdbc:postgresql://" + dbHost_test + ":" + dbPort_test + "/" + dbName_test + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUsername_test, dbPassword_test);
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
