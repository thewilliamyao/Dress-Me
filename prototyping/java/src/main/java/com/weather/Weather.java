//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.weatherapp;

import java.util.Date; 

public class Weather {

    private Double windSpeed;             // windspeed in miles per hour
    private Double humidity;              // relative humidity, [0,1]
    private String precipType;            // "rain", "snow", "sleet", "no data"
    private Double precipProbability;     // probability of precip [0,1]
    private Double precipIntensity;       // inches of liquid per hour
    private Double temperature;           // temp in degrees Fahrenheit
    private Double apparentTemperature;   // "feels like" temp in Fahrenheit

    public Weather(Double windSpeed, Double humidity, String precipType,
        Double precipProbability, Double precipIntensity, Double temperature, Double apparentTemperature) {
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.precipType = precipType;
        this.precipProbability = precipProbability;
        this.precipIntensity = precipIntensity;
        this.temperature = temperature;
        this.apparentTemperature = apparentTemperature;
        // https://api.darksky.net/forecast/[key]/[latitude],[longitude]
        // GET https://api.darksky.net/forecast/0123456789abcdef9876543210fedcba/42.3601,-71.0589
        /*
        ForecastIO fio = new ForecastIO("90d56a872c963f85162f81873b40fbba"); //instantiate the class with the API key.
        fio.setUnits(ForecastIO.UNITS_SI);             //sets the units as SI - optional
        fio.setExcludeURL("hourly,minutely");          //excluded the minutely and hourly reports from the reply
        fio.getForecast("38.7252993", "-9.1500364");   //sets the latitude and longitude - not optional
                                                       //it will fail to get forecast if it is not set
                                                       //this method should be called after the options were set
        System.out.println(fio.getForecast("38.7252993", "-9.1500364"));
        */

    }
    /*
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public Date getCreatedOn() {
        return createdOn;
    }
    */


    /*

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather weather = (Weather) o;

        if (done != weather.done) return false;
        if (id != null ? !id.equals(wea.id) : todo.id != null) return false;
        if (title != null ? !title.equals(todo.title) : todo.title != null) return false;
        return !(createdOn != null ? !createdOn.equals(todo.createdOn) : todo.createdOn != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (done ? 1 : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", done=" + done +
                ", createdOn=" + createdOn +
                '}';
    }
    */
}
