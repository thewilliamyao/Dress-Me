/**
*   The Location of the user.
*/
public class Location{

    /**User latitude.*/
    private double latitude;
    /**User longitude.*/
    private double longitude;

    /**
    *   Location constructor.
    *   @param x User latitude.
    *   @param y User longitude.
    */
    public Location(double x, double y){
        this.latitude = x;
        this.longitude = y;
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
    public void setLatitude(int x){
        this.latitude = x;
    }

    /**
    *   Sets the user Longitude.
    *   @param y user Longitude.
    */
    public void setLongitude(int y){
        this.longitude = y;
    }
}