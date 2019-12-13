package com.example.mcount;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/*
* 定义操作数据库的一些方法
* onCreate          创建表
* insertCost        插入一条账单
* getAllCostData    获取数据库中所有的账单
* deleteAllData     删除数据库中所有数据
* deleteCost        删除一条记录
* onUpgrade         升级数据库
* */

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context){
        super(context, "AccountBook", null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table if not exists daily_cost("+
                "id integer primary key autoincrement, "+
                "cost_date text, "+
                "cost_type text, "+
                "cost_money text)");
    }

    public void insertCost(DailyCost dailyCost){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cost_type", dailyCost.getName());
        cv.put("cost_money", dailyCost.getCost());
        cv.put("cost_date", dailyCost.getDate());
        database.insert("daily_cost",null, cv);
    }

    public Cursor getAllCostData(){
        SQLiteDatabase database = getWritableDatabase();
        return database.query("daily_cost", null, null, null, null, null, "id " + "DESC");
    }

    public void deleteAllData(){
        SQLiteDatabase database = getWritableDatabase();
        database.delete("daily_cost", null, null);
    }

    public void deleteCost(DailyCost dailyCost){
        SQLiteDatabase database = getWritableDatabase();
        String[] tmp = new String[3];
        tmp[0] = dailyCost.getName();
        tmp[1] = dailyCost.getCost();
        tmp[2] = dailyCost.getDate();
        database.delete("daily_cost","cost_type=? and cost_money=? and cost_date=?",tmp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
