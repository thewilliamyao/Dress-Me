package com.serverapp;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import com.github.dvdme.ForecastIOLib.*;

import javax.sql.DataSource;
import java.util.List;


public class WeatherService {

    private Sql2o db;

    private final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    
    public WeatherService(DataSource dataSource) throws WeatherServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        // TODO database stuff
        // try (Connection conn = db.open()) {
        //     String sql = "CREATE TABLE IF NOT EXISTS item (item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        //                  "                                 title TEXT, done BOOLEAN, created_on TIMESTAMP)" ;
        //     conn.createQuery(sql).executeUpdate();
        // } catch(Sql2oException ex) {
        //     logger.error("Failed to create schema at startup", ex);
        //     throw new WeatherServiceException("Failed to create schema at startup", ex);
        // }
    }
    
    public Weather test() throws WeatherServiceException {
        // TODO: format output as we want
        ForecastIO fio = new ForecastIO("90d56a872c963f85162f81873b40fbba"); //instantiate the class with the API key.
        fio.setUnits(ForecastIO.UNITS_US);             //sets the units as SI - optional
        // fio.setExcludeURL("hourly,minutely");          //excluded the minutely and hourly reports from the reply
        fio.getForecast("39.330496", "-76.620046");   //sets the latitude and longitude - not optional
                                                       //it will fail to get forecast if it is not set
                                                       //this method should be called after the options were set
        FIOHourly h = new FIOHourly(fio);
        Double windSpeed = new Double(0);
        Double humidity = new Double(0);
        Double precipIntensity = new Double(0);
        Double precipProbability = new Double(0);
        String precipType = "";
        Double temperature = new Double(0);
        Double apparentTemperature = new Double(0);
        // only assume they will be out for the next 12 hours at most
        for (int i = 0; (i < 12) && (i < h.hours()); i++) {
            FIODataPoint data = h.getHour(i);
            // set all data to max (for double types)
            windSpeed = (data.windSpeed() > windSpeed ? data.windSpeed() : windSpeed);
            humidity = (data.humidity() > humidity ? data.humidity() : humidity);
            precipIntensity = (data.precipIntensity() > precipIntensity ? data.precipIntensity() : precipIntensity);
            precipProbability = (data.precipProbability() > precipProbability ? data.precipProbability() : precipProbability);
            temperature = (data.temperature() > temperature ? data.temperature() : temperature);
            apparentTemperature = (data.apparentTemperature() > apparentTemperature ? data.apparentTemperature() : apparentTemperature);
            precipType = ((precipType.equals("") || precipType.equals("no data")) ? data.precipType() : precipType);
        }
        precipType = precipType.replace("\"", "");
        return new Weather(windSpeed, humidity, precipType, precipProbability, precipIntensity, temperature, apparentTemperature);
    }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class WeatherServiceException extends Exception {
        public WeatherServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * This Sqlite specific method returns the number of rows changed by the most recent
     * INSERT, UPDATE, DELETE operation. Note that you MUST use the same connection to get
     * this information
     */
    private int getChangedRows(Connection conn) throws Sql2oException {
        return conn.createQuery("SELECT changes()").executeScalar(Integer.class);
    }
}
