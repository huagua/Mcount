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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

/*
* 主页面：显示账单列表
* */

public class MainActivity extends AppCompatActivity {
    private TextView testContent;
    ArrayList<DailyCost> data = new ArrayList<>();

    private DataBaseHelper mDataBaseHelper;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper helper;


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
        //mDataBaseHelper.deleteAllData();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MyAdapter(getData());

        //实现侧滑删除
        helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                Collections.swap(data,viewHolder.getAdapterPosition(),target.getAdapterPosition());
                mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑事件
                mDataBaseHelper.deleteCost(data.get(viewHolder.getAdapterPosition()));
                data.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return true;
            }
        });
    }

    private void initView() {
        mRecyclerView =findViewById(R.id.cost_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);

        helper.attachToRecyclerView(mRecyclerView);

        //设置系统默认动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    private ArrayList<DailyCost> getData() {
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
                DailyCost tmpDaily = new DailyCost(R.drawable.duihao);
                tmpDaily.setName(cursor.getString(cursor.getColumnIndex("cost_type")));
                tmpDaily.setCost(cursor.getString(cursor.getColumnIndex("cost_money")));
                tmpDaily.setDate(cursor.getString(cursor.getColumnIndex("cost_date")));
                data.add(tmpDaily);
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
