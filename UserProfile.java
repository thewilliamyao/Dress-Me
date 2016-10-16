/**
* The user's profile, information, and daily recommendations
*/
public class UserProfile{
    /**User email*/
    private String email;
    /**User account password.*/
    private String password;
    /**User's list of cloethes in closet*/
    private Close closet;
    /**User's list of clothes in laundry*/
    private Laundry laundry;
    /**Generated recommendation for the weather*/
    private Recommendation recommendation;
    /**User's current location*/
    private Location currrentLocation;

    /**
    *   Constructor for UserProfile
    */
    public UserProfile(){}

    /**
    *   Lets user change their email.
    */
    public void updateEmail(String s){

    }

    /**
    *   Lets user change their password.
    */
    public void updatePassword(String s){

    }

    /**
    *   Lets user update their current closet.
    *   @param c the closet to update.
    *   @return the updated closet.
    */
    public Closet updateCloset(Closet c){

    }

    /**
    *   Lets user update their current laundry.
    *   @param l the laundry to update.
    *   @return the updated laundry.
    */
    public Laundry updateLaundry(Laundry l){

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

    }

    /**
    *   Provides the daily recommendation.
    *   @param c the closet to choose from.
    *   @param l the laundry to avoid.
    *   @param loc the location of the user.
    *   @return the recommended daily outfit.
    */
    public Recommendation getRecommendation(Closet c, Laundry l, Location loc){
        
    }
}
