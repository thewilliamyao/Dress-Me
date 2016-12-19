package com.server;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import com.github.dvdme.ForecastIOLib.*;

import javax.sql.DataSource;
import java.util.List;
public class Weather implements Comparable<DaySummary> {
    private Double windSpeed;              // windspeed in miles per hour
    private Double humidity;               // relative humidity, [0,1]
    private String precipType;             // "rain", "snow", "sleet", "no data"
    private Double precipProbability;      // probability of precip [0,1]
    private Double precipIntensity;        // inches of liquid per hour
    private Double maxTemp;         // max temp in degrees Fahrenheit
    private Double maxApparentTemp; // max "feels like" temp in Fahrenheit
    private Double minTemp;         // min temp in degrees Fahrenheit
    private Double minApparentTemp; // min "feels like" temp in Fahrenheit

    /**
    *   Constructor for weather.
    *   @param windSpeed            The speed of the wind.
    *   @param humidity             The location humidity.
    *   @param precipType           The location precipitation type.
    *   @param precipProbability    The location precipitation probability.
    *   @param precipIntensity      The location precipitation Intensity.
    *   @param temperature          The location temperature.
    *   @param apparentTempreature  The location apperent temperature
    */
    // TODO remove this; deprecated until we add in minTemp and minApparentTemp
    public Weather(Double windSpeed, Double humidity, String precipType,
        Double precipProbability, Double precipIntensity, Double temperature, Double apparentTemperature) {
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.precipType = precipType;
        this.precipProbability = precipProbability;
        this.precipIntensity = precipIntensity;
        this.maxTemp = temperature;
        this.maxApparentTemp = apparentTemperature;
    }

    // for now, return a weather object with the hardcoded location of baltimore.
    public Weather(double latitude, double longitude) {
        ForecastIO fio = new ForecastIO("90d56a872c963f85162f81873b40fbba"); //instantiate the class with the API key.
        fio.setUnits(ForecastIO.UNITS_US);             //sets the units as SI - optional
        fio.getForecast(String.valueOf(latitude), String.valueOf(longitude)); // location of baltimore

        FIOHourly h = new FIOHourly(fio);
        this.windSpeed = new Double(0);
        this.humidity = new Double(0);
        this.precipIntensity = new Double(0);
        this.precipProbability = new Double(0);
        this.precipType = "";
        this.maxTemp = new Double(0);
        this.maxApparentTemp = new Double(0);
        this.minTemp = new Double(0);
        this.minApparentTemp = new Double(0);
        // only assume they will be out for the next 12 hours at most
        for (int i = 0; (i < 12) && (i < h.hours()); i++) {
            FIODataPoint data = h.getHour(i);
            // set all data to max (for double types)
            this.windSpeed = (data.windSpeed() > this.windSpeed ? data.windSpeed() : this.windSpeed);
            this.humidity = (data.humidity() > this.humidity ? data.humidity() : this.humidity);
            this.precipIntensity = (data.precipIntensity() > this.precipIntensity ? data.precipIntensity() : this.precipIntensity);
            this.precipProbability = (data.precipProbability() > this.precipProbability ? data.precipProbability() : this.precipProbability);
            this.maxTemp = (data.temperature() > this.maxTemp ? data.temperature() : this.maxTemp);
            this.maxApparentTemp = (data.apparentTemperature() > this.maxApparentTemp ? data.apparentTemperature() : this.maxApparentTemp);
            this.minTemp = (data.temperature() > this.minTemp ? data.temperature() : this.minTemp);
            this.minApparentTemp = (data.apparentTemperature() > this.minApparentTemp ? data.apparentTemperature() : this.minApparentTemp);
            this.precipType = ((this.precipType.equals("") || this.precipType.equals("no data")) ? data.precipType() : this.precipType);
        }
        this.precipType = this.precipType.replace("\"", "");
    }

    public int compareTo(DaySummary d) {
    // Factors being compared: temperature, apparentTemp, humidity, windSpeed?, precipIntensity
    // Take precipChance into consideration in algorithm, but not similarity index b/c it is due to chance

    int tempDiff = (int) (Math.round(this.maxTemp - d.getMaxTemp()));
    int apparentTempDiff = (int) (Math.round(this.maxApparentTemp - d.getMaxApparentTemp()));

    return tempDiff + apparentTempDiff;
    //int maxFactor = Math.max(Math.abs(tempDiff), Math.abs(apparentTempDiff));
    
    //int humidityDiff = (int) (Math.round(this.humidity - w.getHumidity()));       
    }

    public double getComparisonValue() {
    return this.maxTemp + this.maxApparentTemp;
    }

    public double getWindSpeed() {
        return this.windSpeed;
    }
    public double getHumidity() {
        return this.humidity;
    }
    public String getPrecipType() {
        return this.precipType;
    }
    public double getPrecipProb() {
        return this.precipProbability;
    }
    public double getPrecipIntensity() {
        return this.precipIntensity;
    }
    public double getMaxTemp() {
        return this.maxTemp;
    }
    public double getMaxApparentTemp() {
        return this.maxApparentTemp;
    }
    public double getMinTemp() {
        return this.minTemp;
    }
    public double getMinApparentTemp() {
        return this.minApparentTemp;
    }
}
