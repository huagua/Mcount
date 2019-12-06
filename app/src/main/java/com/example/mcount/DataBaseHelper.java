package com.example.mcount;

import android.content.Context;
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
                "cost_title varchar, "+
                "cost_date varchar, "+
                "cost_money varchar)");
    }

    public void insertCost(){

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
