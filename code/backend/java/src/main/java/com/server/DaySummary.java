package com.server;

/**
 * Class containing data for a day summarizing weather and clothes worn
 */
public class DaySummary {
    private int userId;
    /** Day's max temperature.*/    
    private Double maxTemp;
    /** Day's max apparent temperature.*/
    private Double maxApparentTemp;
    /** The outfit that was worn.*/
    private HashMap<String, String> outfit;    

    public DaySummary(int userId, Weather weather) {
	this.userId = userId;
	this.maxTemp = weather.getMaxTemp();
	this.maxApparentTemp = weather.getMaxApparentTemp();
	this.outfit = new HashMap<>();
    }

    public DaySummary(int userId, Double maxTemp, Double maxApparentTemp, String top, String pants,
		      String footwear, String accessory, String outerwear) {
	this.userId = userId;
	this.maxTemp = maxTemp;
	this.maxApparentTemp = maxApparentTemp;	

	this.outfit = new HashMap<>();
	this.outfit.put("top", top);
	this.outfit.put("pants", pants);
	this.outfit.put("footwear", footwear);
	this.outfit.put("accessory", accessory);
	this.outfit.put("outerwear", outerwear);	
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
    public void setMaxTemp(int maxApparentTemp) {
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
     *   @param the HashMap object representing this outfit.
     */
    public void setOutfit(HashMap<String, String> outfit) {
	this.outfit = outfit;
    }
}
