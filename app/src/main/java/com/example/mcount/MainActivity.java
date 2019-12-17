package com.example.mcount;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    ArrayList<DailyCost> data = new ArrayList<>();

    private DataBaseHelper mDataBaseHelper;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper helper;
    private TextView total;
    private Button caidanButton;

    private Double totalAccount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBaseHelper = new DataBaseHelper(this);

        if(Build.VERSION.SDK_INT>=23){  //版本号判断（以下功能只在21及以上版本实现）
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);   //活动的布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT);   //将状态栏设置为透明色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//将状态栏字体设为黑色，该功能在23及以上版本实现
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

        caidanButton = findViewById(R.id.caidan_button);
        caidanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, WriteDown.class);
                startActivityForResult(intent,1);
            }
        });

        initData();
        initView();

    }


    //初始化数据
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

                //删除之后重新设置总花销金额
                totalAccount -= Double.parseDouble(data.get(viewHolder.getAdapterPosition()).getCost());
                total = findViewById(R.id.test_content);
                String totalString = Double.toString(totalAccount);
                total.setText(totalString.substring(0,totalString.indexOf(".")+3));

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

    //初始化账单列表
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

    //增加一条记录后进行刷新
    public void updateAfterAddOne(){
        data.clear();       //清除原data中的内容
        queryMsql();        //从sql中重新请求数据
        mAdapter.notifyDataSetChanged();        //更新recyclerView
    }

    //搜索数据库
    public void queryMsql(){
        totalAccount = 0.0;
        Cursor cursor = mDataBaseHelper.getAllCostData();
        if(cursor != null){
            while(cursor.moveToNext()){
                DailyCost tmpDaily = new DailyCost(R.drawable.duihao);
                tmpDaily.setName(cursor.getString(cursor.getColumnIndex("cost_type")));
                tmpDaily.setCost(cursor.getString(cursor.getColumnIndex("cost_money")));
                tmpDaily.setDate(cursor.getString(cursor.getColumnIndex("cost_date")));
                data.add(tmpDaily);
                if(!tmpDaily.getCost().equals("")){
                    totalAccount += Double.parseDouble(tmpDaily.getCost());
                }
            }
        }

        total = findViewById(R.id.test_content);
        String totalString = Double.toString(totalAccount);
        total.setText(totalString.substring(0,totalString.indexOf('.')+3));

        cursor.close();
    }

    //从编辑页面传送信息到主页面，如果resultCode为2就刷新列表
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 2){
            updateAfterAddOne();
        }
    }

}
