package com.example.mcount;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView testContent;
    ArrayList<DailyCost> data = new ArrayList<>();

    private DataBaseHelper mDataBaseHelper;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBaseHelper = new DataBaseHelper(this);

        if(Build.VERSION.SDK_INT>=21){  //版本号判断（以下功能只在21及以上版本实现）
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);   //活动的布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT);   //将状态栏设置为透明色
        }

        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteDown.class);
                startActivityForResult(intent,1);
            }
        });

        initData();
        initView();
    }

    private void initData() {
        mDataBaseHelper.deleteAllData();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MyAdapter(getData());
    }

    private void initView() {
        mRecyclerView =findViewById(R.id.cost_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);
    }


    /*
    private ArrayList<DailyCost> getData() {
        String tmp = "";
        for(int i = 0; i < 10; i++) {
            data.add(new DailyCost("支出", R.drawable.duihao,(tmp+i),"2012-12-12"));
            data.add(new DailyCost("收入", R.drawable.duihao,(tmp+i+10), "2012-12-11"));
        }

        return data;
    }
     */

    private ArrayList<DailyCost> getData() {
        String tmp = "";
        for(int i = 0; i < 10; i++) {
            DailyCost tmpdaily = new DailyCost(R.drawable.duihao);
            tmpdaily.setDate("2012-11-24");
            tmpdaily.setName("支出");
            tmpdaily.setCost(tmp+i);
            mDataBaseHelper.insertCost(tmpdaily);
        }

        queryMsql();

        return data;
    }

    public void updateAfterAddOne(){
        data.clear();       //清除原data中的内容
        queryMsql();        //从sql中重新请求数据
        mAdapter.notifyDataSetChanged();        //更新recyclerView
    }

    public void queryMsql(){
        Cursor cursor = mDataBaseHelper.getAllCostData();
        if(cursor != null){
            while(cursor.moveToNext()){
                DailyCost tmpdaily = new DailyCost(R.drawable.duihao);
                tmpdaily.setName(cursor.getString(cursor.getColumnIndex("cost_type")));
                tmpdaily.setCost(cursor.getString(cursor.getColumnIndex("cost_money")));
                tmpdaily.setDate(cursor.getString(cursor.getColumnIndex("cost_date")));
                data.add(tmpdaily);
            }
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 2){
            updateAfterAddOne();
        }else if(requestCode == 3){
            String content = data.getStringExtra("data");
            testContent = findViewById(R.id.test_content);
            testContent.setText(content);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
