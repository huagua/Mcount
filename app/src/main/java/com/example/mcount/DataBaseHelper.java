package com.example.mcount;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
