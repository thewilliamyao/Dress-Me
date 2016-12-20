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

    /**
    * Creates a new user.
    * @param userId the id for the user.
    * @param email the email for the user.
    * @param password the password for the user.
    */
    public User(int userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }
    /**
    *   Constructor for User
    *   @param email the email for the user.
    *   @param password the password for the user.
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
    *   @param id the user's id
    */
    public void setUserId(int id) {
        this.userId = id;
    }

    /**
    * Gets the user id
    * @return the id of the user
    */
    public int getUserId() {
        return this.userId;
    }

    /**
    *   Get the user's email.
    *   @return the user's email.
    */
    public String getEmail(){
        return email;
    }
    
    /**
    *   Get the user's password. For testing purposes only.
    *   @return the user's password.
    */
    public String getPassword(){
        return password;
    }
}
