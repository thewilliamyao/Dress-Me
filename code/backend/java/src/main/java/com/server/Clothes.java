package com.serverapp;

/**
* The Clothes class
*/
public class Clothes{
    private int userId;
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
    public void print() {
        System.out.println("userId: " + userId + ", type: " + type + ", specificType: " + specificType + ", numberOwned: " + numberOwned);
    }

    public int getUserId() {
        return this.userId;
    }

    public int getClothesId() {
        return this.clothesId;
    }
    /**
    *   Gets the type of this clothe.
    *   @return the type of clothe.
    */
    public String getType(){
        return this.type;
    }
    /**
    *   Gets the type of this clothe.
    *   @return the type of clothe.
    */
    public String getSpecificType(){
        return this.specificType;
    }

    /**
    *   Gets the number of this type of clothe.
    *   @return the number of this type of clothe.
    */
    public int getNumberOwned(){
        return this.numberOwned;
    }

    /**
    *   Gets the number of this type that are dirty.
    *   @return the number dirty.
    */
    public int getNumberDirty(){
        return this.numberDirty;
    }

    /**
    *   Gets the max temp for this type of clothing.
    *   @return the max temp.
    */
    public double getTempHigh(){
        return this.tempHigh;
    }

    /**
    *   Gets the min temp for this type of clothing.
    *   @return the min temp.
    */
    public double getTempLow(){
        return this.tempLow;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setClothesId(int clothesId) {
        this.clothesId = clothesId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSpecificType(String specificType) {
        this.specificType = specificType;
    }
    /**
    *   Sets how many of this type are owned.
    *   @param x number owned.
    */
    public void setNumberOwned(int numberOwned){
        this.numberOwned = numberOwned;
    }

    /**
    *   Sets how many of this type are dirty.
    *   @param x number dirty.
    */
    public void setNumberDirty(int numberDirty){
        this.numberDirty = numberDirty;
    }

    /**
    *   Sets the max temp for this type of clothing.
    *   @param x the max temp.
    */
    public void setTempHigh(double tempHigh){
        this.tempHigh = tempHigh;
    }

    /**
    *   Sets the min temp for this type of clothing.
    *   @param x the min temp.
    */
    public void setTempLow(double tempLow){
        this.tempLow = tempLow;
    }

}