package com.example.mcount;

public class DailyCost {
    private String name;
    private int imageId;
    private String cost;

    public DailyCost(String name, int imageId, String cost){
        this.name = name;
        this.imageId = imageId;
        this.cost = cost;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }

    public String getCost(){
        return cost;
    }
}
