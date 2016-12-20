package com.server;

/**
*   The Location of the user.
*/
public class Location{
    private int userId;
    private int locationId;
    /**User latitude.*/
    private double latitude;
    /**User longitude.*/
    private double longitude;

    /**
    *   Location constructor.
    *   @param userId the id of the user.
    *   @param locationId the new Id for the location.
    *   @param x User latitude.
    *   @param y User longitude.
    */
    public Location(int userId, int locationId, double x, double y){
        this.userId = userId;
        this.locationId = locationId;
        this.latitude = x;
        this.longitude = y;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
    public int getUserId() {
        return this.userId;
    }

    public int getLocationId() {
        return this.locationId;
    }

    public void print() {
        System.out.println("uid: " + this.userId + ", lid: " + this.locationId + ", lat: " + this.latitude + ", lon: " + this.longitude);
    }

    /**
    *   Gets the user Latitude.
    *   @return user Latitude.
    */
    public double getLatitude(){
        return this.latitude;
    }

    /**
    *   Gets the user Longitude.
    *   @return user Longitude.
    */
    public double getLongitude(){
        return this.longitude;
    }

    /**
    *   Sets the user Latitude.
    *   @param x user Latitude.
    */
    public void setLatitude(double x){
        this.latitude = x;
    }

    /**
    *   Sets the user Longitude.
    *   @param y user Longitude.
    */
    public void setLongitude(double y){
        this.longitude = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Location.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Location other = (Location) obj;
        if (this.getUserId() != other.getUserId()) {
            return false;
        }
        if (this.getLocationId() != other.getLocationId()) {
            return false;
        }
        if (this.getLatitude() != other.getLatitude()) {
            return false;
        }
        if (this.getLongitude() != other.getLongitude()) {
            return false;
        }
        return true;
    }
}
