/**
*   The weather class containing all weather information.
*/
public class Weather{
    /**The speed of the wind.*/
    private double windSpeed;
    /**The location humidity.*/
    private double humidity;
    /**The location precipitation type.*/
    private double precipType;
    /**The location precipitation probability.*/
    private double precipProbability;
    /**The location precipitation Intensity.*/
    private double precipIntensity;
    /**The location temperature.*/
    private double temperature;
    /**The location apperent temperature.*/
    private double apparentTemperature;

    /**
    *   Constructor for weather.
    *   @param wS The speed of the wind.
    *   @param h  The location humidity.
    *   @param pT The location precipitation type.
    *   @param pP The location precipitation probability.
    *   @param pI The location precipitation Intensity.
    *   @param temp   The location temperature.
    *   @param aTemp  The location apperent temperature
    */
    public Weather(double wS, double h, double pT, double pP, double pI, double temp, double aTemp ){
        this.windSpeed = wS;
        this.humidity = h;
        this.precipType = pT;
        this.precipProbability = pP;
        this.precipIntensity = pI;
        this.temperature = temp;
        this.apparentTemperature = aTemp;
    }

    /**
    *   Changes weather based on user change in location.
    *   @param l the new location.
    */
    public void updateData(Location l){

    }
}