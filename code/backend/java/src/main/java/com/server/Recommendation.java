package com.serverapp;

/**
*   The list of recommendations given to user.
*/

import java.util.*;
public class Recommendation{
    /**List of recommended clothes*/
    private String top;
    private String pants;
    private String footwear;
    private String accessory;
    private String outerwear;

    public Recommendation() {
        this.top = "";
        this.pants = "";
        this.footwear = "";
        this.accessory = "";
        this.outerwear = "";
    }
    /**
    *   Recommendations class constructor
    */
    public Recommendation(Clothes top, Clothes pants, Clothes footwear, Clothes accessory, Clothes outerwear) {

    }

    public Recommendation(String top, String pants, String footwear, String accessory, String outerwear) {
        this.top = top;
        this.pants = pants;
        this.footwear = footwear;
        this.accessory = accessory;
        this.outerwear = outerwear;
    }


}