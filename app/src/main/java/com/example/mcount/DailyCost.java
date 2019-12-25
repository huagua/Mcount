package com.example.mcount;

/*
    定义了账单类：类型name、类型图标imageId、金额cost、日期date
    并定义了相应的方法
 */

public class DailyCost {
    private String name;
    private int imageId;
    private String cost;
    private String date;
    private String time;

    public DailyCost(String name, int imageId, String cost,String date,String time){
        this.name = name;
        this.imageId = imageId;
        this.cost = cost;
        this.date = date;
        this.time = time;

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

    public void setTime(String time){
        this.time = time;
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

    public String getTime(){
        return time;
    }

}
