package com.server;

/**
* The Clothes class
*/
public class Clothes{
    /**The id of the user that this clothes item belongs to*/
    private int userId;
    /**The id of this clothes item*/
    private int clothesId;
    /**Type of clothe.*/
    private String type;
    /**Type of clothe.*/
    private String specificType;
    /**How many owned of this type.*/
    private int numberOwned;
    /**How many dirty of this type.*/
    private int numberDirty;
    /**User's high temperature for this type.*/
    private double tempHigh;
    /**User's low temperatur for this type.*/
    private double tempLow;
    /**Number of times this item was worn*/
    private int timesWorn;
    /**Number of times this item can be worn*/
    private int maxTimesWorn;

    /**
     *   Minimal constructor for a single items of clothing.
     */
    public Clothes (String specificType) {
        this.specificType = specificType;
    }
    
    /*
    * Constructor for clothes.
    */
    public Clothes(int userId, int clothesId, String type, String specificType, int numberOwned, int numberDirty,
        double tempHigh, double tempLow) {
        this.userId = userId;
        this.clothesId = clothesId;
        this.type = type;
        this.specificType = specificType;
        this.numberOwned = numberOwned;
        this.numberDirty = numberDirty;
        this.tempHigh = tempHigh;
        this.tempLow = tempLow;
    }

    /**
    * Debug method used to print some fields in this class.
    */
    public void print() {
        System.out.println("userId: " + userId + ", type: " + type + ", specificType: " + specificType + ", numberOwned: " + numberOwned);
    }

    /**
    * Returns the user Id.
    * @return the id of the user
    */
    public boolean equals(Clothes c) {
        return this.specificType.equals(c.getSpecificType());
    }

    public int hashCode() {
        return specificType.hashCode();
    }

    public int getUserId() {
        return this.userId;
    }

    /**
    * Returns the clothes item id.
    * @return the id of the clothes.
    */
    public int getClothesId() {
        return this.clothesId;
    }

    /**
    * Gets the type of this clothes.
    * @return the type of clothes.
    */
    public String getType() {
        return this.type;
    }
    
    /**
    * Gets the specific type of this clothes item.
    * @return the specific type of the clothes item.
    */
    public String getSpecificType() {
        return this.specificType;
    }

    /**
    * Gets the number owned of this clothes item..
    * @return the number owned..
    */
    public int getNumberOwned() {
        return this.numberOwned;
    }

    /**
    * Gets the number of this type that are dirty.
    * @return the number dirty.
    */
    public int getNumberDirty() {
        return this.numberDirty;
    }

    /**
    * Gets the max temp for this type of clothing.
    * @return the max temp.
    */
    public double getTempHigh() {
        return this.tempHigh;
    }

    /**
    * Gets the min temp for this type of clothing.
    * @return the min temp.
    */
    public double getTempLow() {
        return this.tempLow;
    }
    
    /**
    * Gets the number of times worn.
    * @return the number of times worn
    */
    public int getTimesWorn() {
        return this.timesWorn;
    }

    /**
    * Gets the number of times this item can be worn before marking dirty.
    * @return the number of times this item can be worn before it's marked dirty.
    */
    public int getMaxTimesWorn() {
        return this.maxTimesWorn;
    }

    /**
    * Sets the user id.
    * @param userId the userId you wish to set.
    */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
    * Sets the id of the clothes item.
    * @param clothesId the id you wish to set.
    */
    public void setClothesId(int clothesId) {
        this.clothesId = clothesId;
    }

    /**
    * Sets the type of the clothes item
    * @param type the type you want to set.
    */
    public void setType(String type) {
        this.type = type;
    }

    /**
    * Sets the specific type of this clothing item.
    * @param specificType the specific type of clothing item. 
    */
    public void setSpecificType(String specificType) {
        this.specificType = specificType;
    }

    /**
    *   Sets how many of this type are owned.
    *   @param numberOwned number owned.
    */
    public void setNumberOwned(int numberOwned){
        this.numberOwned = numberOwned;
    }

    /**
    *   Sets how many of this type are dirty.
    *   @param numberDirty number dirty.
    */
    public void setNumberDirty(int numberDirty){
        this.numberDirty = numberDirty;
    }

    /**
    *   Sets the max temp for this type of clothing.
    *   @param tempHigh the max temp.
    */
    public void setTempHigh(double tempHigh){
        this.tempHigh = tempHigh;
    }

    /**
    *   Sets the min temp for this type of clothing.
    *   @param tempLow the min temp.
    */
    public void setTempLow(double tempLow) {
        this.tempLow = tempLow;
    }

    /**
    *   Sets the times worn for this type of clothing.
    *   @param timesWorn the times worn
    */
    public void setTimesWorn(int timesWorn) {
        this.timesWorn = timesWorn;
    }

    /**
    *   Sets the max times worn for this type of clothing.
    *   @param maxTimesWorn the maximum times one can wear this before it is dirty
    */
    public void setMaxTimesWorn(int maxTimesWorn) {
        this.maxTimesWorn = maxTimesWorn;
    }
}
