package com.server;
import java.util.*;

/**
*   The list of recommendations given to user.
*/
public class Recommendation {
    /**List of recommended clothes*/
    private String top;
    private String pants;
    private String footwear;
    private String accessory;
    private String outerwear;

    public Recommendation() {
    }

    public Recommendation (String top, String pants, String footwear, String accessory, String outerwear) {
        this.top = top;
        this.pants = pants;
        this.footwear = footwear;
        this.accessory = accessory;
        this.outerwear = outerwear;
    }
    
    public void print() {
        System.out.println("TOP: " + this.top + ", PANTS: " + this.pants + ", FOOTWEAR: " +
            this.footwear + ", ACCESSORY: " + this.accessory + ", OUTER: " + this.outerwear);
    }

    public List<Clothes> asClothesList() {
        List<Clothes> outfit = new ArrayList<>();
        outfit.add(new Clothes(this.top, "top"));
        outfit.add(new Clothes(this.pants, "pants"));
        outfit.add(new Clothes(this.footwear, "footwear"));
        outfit.add(new Clothes(this.accessory, "accessory"));
        outfit.add(new Clothes(this.outerwear, "outerwear"));
        return outfit;
    }

    public void setItem(Clothes item) {
        String specificType = item.getSpecificType();
        switch (item.getType()) {
            case "top":
                setTop(specificType);
                break;
            case "pants":
                setPants(specificType);
                break;
            case "footwear":
                setFootwear(specificType);
                break;
            case "accessory":
                setAccessory(specificType);
                break;
            case "outerwear":
                setOuterwear(specificType);
                break;
            default:
                System.out.println("Invalid clothing item.\n");
                break;
        }
    }

    public boolean hasItem(Clothes item) {
        switch (item.getType()) {
            case "top":
                return this.top.equals(item.getSpecificType());
            case "pants":
                return this.pants.equals(item.getSpecificType());
            case "footwear":
                return this.footwear.equals(item.getSpecificType());
            case "accessory":
                return this.accessory.equals(item.getSpecificType());
            case "outerwear":
                return this.outerwear.equals(item.getSpecificType());
            default:
                System.out.println("Invalid clothing item.\n");
                return false;
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!Recommendation.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        final Recommendation r = (Recommendation) o;
        return this.asClothesList().equals(r.asClothesList());
    }
    
    public void setTop(String top) {
        this.top = top;
    }
    public void setPants(String pants) {
        this.pants = pants;
    }
    public void setFootwear(String footwear) {
        this.footwear = footwear;
    }
    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }
    public void setOuterwear(String outerwear) {
        this.outerwear = outerwear;
    }

    public String getTop() {
        return this.top;
    }
    public String getPants() {
        return this.pants;
    }
    public String getFootwear() {
        return this.footwear;
    }
    public String getAccessory() {
        return this.accessory;
    }
    public String getOuterwear() {
        return this.outerwear;
    }
}
