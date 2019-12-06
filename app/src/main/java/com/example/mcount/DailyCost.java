package com.example.mcount;

public class DailyCost {
    private String name;
    private int imageId;
    private String cost;
    private String date;

    public DailyCost(String name, int imageId, String cost,String date){
        this.name = name;
        this.imageId = imageId;
        this.cost = cost;
        this.date = date;
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

    public void setDate(String date){
        this.date = date;
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

    public String getDate(){
        return date;
    }
}
