package com.server;
import java.util.HashMap;

/**
 * Class containing data for a day summarizing weather and clothes worn
 */
public class DaySummary {
    private int daySummaryId;
    private int userId;
    /** Day's max temperature.*/    
    private Double maxTemp;
    /** Day's max apparent temperature.*/
    private Double maxApparentTemp;
    /** The outfit that was worn.*/
    private String top;
    private String pants;
    private String footwear;
    private String accessory;
    private String outerwear;
//    private Recommendation outfit;

    /*public DaySummary(int userId, Weather weather, Recommendation rec) {
    this.userId = userId;
    this.maxTemp = weather.getMaxTemp();
    this.maxApparentTemp = weather.getMaxApparentTemp();
    this.outfit = rec.clone();
    }*/

    public DaySummary(int daySummaryId, int userId, double maxTemp, double maxApparentTemp, String top, String pants,
              String footwear, String accessory, String outerwear) {
        this.daySummaryId = daySummaryId;
        this.userId = userId;
        this.maxTemp = maxTemp;
        this.maxApparentTemp = maxApparentTemp; 
        this.top = top;
        this.pants = pants;
        this.footwear = footwear;
        this.accessory = accessory;
        this.outerwear = outerwear;
        //this.outfit = new Recommendation(top, pants, footwear, accessory, outerwear);
    }

    public String toString() {
        System.out.printf("top: %s\n", this.top);
        System.out.printf("pants: %s\n", this.pants);
        System.out.printf("footwear: %s\n", this.footwear);
        System.out.printf("accesory: %s\n", this.accessory);
        System.out.printf("outerwear: %s\n", this.outerwear);
        System.out.printf("maxTemp: %f\n", this.maxTemp);
        System.out.printf("maxApparentTemp: %f\n", this.maxApparentTemp);
        return this.top + "," + this.pants + "," + this.footwear + "," + this.accessory + "," + this.outerwear + ",[" + this.maxTemp.toString() + "," +  this.maxApparentTemp.toString() + "]";
    }

    public double getComparisonValue() {
        return this.maxTemp + this.maxApparentTemp;
    }

    /**
     *   Gets the ID of the user this summary belongs to.
     *   @return the user's ID.
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     *   Gets the maximum temperature from this day.
     *   @return the max temp.
     */
    public Double getMaxTemp() {
        return this.maxTemp;
    }

    /**
     *   Gets the maximum apparent temperature from this day.
     *   @return the max apparent temp.
     */
    public Double getMaxApparentTemp() {
        return this.maxApparentTemp;
    }

    /**
     *   Gets the recommendation that was chosen by the user on this day.
     *   @return the recommendation.
     */
    public Recommendation getRecommendation() {
        return new Recommendation(this.top, this.pants, this.footwear, this.accessory, this.outerwear);
    }
    

    public void setDaySummaryId(int daySummaryId) {
        this.daySummaryId = daySummaryId;
    }

    public void setTop(String top) {
        this.top = top;
    }
    public void setPants(String pants) {
        this.pants = pants;
    }
    public void setFootwear(String footwear) {
        this.footwear = footwear;
    }
    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }
    public void setOuterwear(String outerwear) {
        this.outerwear = outerwear;
    }
    /**
     *   Sets the user ID.
     *   @param the new user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     *   Sets the max temperature.
     *   @param the new max temperature.
     */
    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }

    /**
     *   Sets the max apparent temperature.
     *   @param the new max apparent temperature.
     */
    public void setMaxApparentTemp(Double maxApparentTemp) {
        this.maxApparentTemp = maxApparentTemp;
    }

    /**
     *   Sets the outfit.
     *   @param the Recommendation object representing this outfit.
     */
    /*
    public void setOutfit(Recommendation outfit) {
        this.outfit = outfit;
    }
    */
}
