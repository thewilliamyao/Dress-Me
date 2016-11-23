package com.server;

/**
* The user's profile, information, and daily recommendations
*/
public class User {
    private int userId;
    /**User email*/
    private String email;
    /**User account password.*/
    private String password;

    /**User's list of cloethes in closet*/
    // private Closet closet;
    /**User's list of clothes in laundry*/
    // private Laundry laundry;
    /**Generated recommendation for the weather*/
    // private Recommendation recommendation;
    /**User's current location*/
    // private Location currrentLocation;

    public User(int userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }
    /**
    *   Constructor for User
    */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void print() {
        System.out.println("uid: " + this.userId + ", email: " + this.email + ", pass: " + this.password);
    }
    /**
    *   Sets the user's Id
    *   @param id, the user's id
    */
    public void setUserId(int id) {
        this.userId = id;
    }


    /**
    *   Get the user's email.
    *   @return the user's email.
    */
    public String getEmail(){
	return email;
    }
    
    /**
    *   Lets user change their email.
    *   @param s the new email
    */
    public void updateEmail(String s){

    }

    /**
    *   Get the user's password. For testing purposes only.
    *   @return the user's password.
    */
    public String getPassword(){
	return password;
    }
    
    /**
    *   Lets user change their password.
    *   @param s the new password
    */
    public void updatePassword(String s){

    }

    /**
    *   Lets user update their current closet.
    *   @param c the closet to update.
    *   @return the updated closet.
    */
    public Closet updateCloset(Closet c){
        return null;
    }

    /**
    *   Lets user update their current laundry.
    *   @param l the laundry to update.
    *   @return the updated laundry.
    */
    public Laundry updateLaundry(Laundry l){
        return null;
    }

    /**
    *   Lets user update their current location.
    *   @param x the user longitude.
    *   @param y the user latitude.
    */
    public void setLocation(double x, double y){

    }

    /**
    *   Retrieves user's location.
    *   @return the user's location.
    */
    public Location getLocation(){
        return null;
    }

    /**
    *   Provides the daily recommendation.
    *   @param c the closet to choose from.
    *   @param l the laundry to avoid.
    *   @param loc the location of the user.
    *   @return the recommended daily outfit.
    */
    public Recommendation getRecommendation(Closet c, Laundry l, Location loc){
        return null;
    }
}
