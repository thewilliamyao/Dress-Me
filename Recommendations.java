/**
*   The list of recommendations given to user.
*/
public class Recommendations{
    /**List of recommended clothes*/
    private List<Clothes> clothesList;

    /**
    *   Recommendations class constructor
    */
    public Reommendations(){

    }

    /**
    *   Creates the recommendations and adds to the List,
    *   based on user's clothes, preference, and location of weather.
    *   @param c the closet to choose from.
    *   @param l the laundry to avoid.
    *   @param loc the user location
    *   @param comfortableHighTemp max temp of user.
    *   @param comfortableLowTemp min temp of user.
    */
    public void makeRecommendation(Closet c, Laundry l, Location loc, double comfortableHighTemp, double comfortableLowTemp){

    }

    /**
    *   Returns the reommended list of outfit.
    *   @return List the list of clothes to wear.
    */
    public List getRecommendation(){

    }
}