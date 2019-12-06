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

    public DailyCost(int imageId){
        this.imageId = imageId;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCost(String cost){
        this.cost = cost;
    }

    public void setImageId(int imageId){
        this.imageId = imageId;
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
