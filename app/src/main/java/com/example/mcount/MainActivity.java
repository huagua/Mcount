package com.example.mcount;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/*
* 主页面：显示账单列表
* */

public class MainActivity extends AppCompatActivity {
    ArrayList<DailyCost> dataAll = new ArrayList<>();  //全部数据
    ArrayList<DailyCost> dataDate = new ArrayList<>();  //今日全部数据
    ArrayList<DailyCost> dataWeek = new ArrayList<>();  //本周全部数据
    ArrayList<DailyCost> dataMonth = new ArrayList<>();  //本月全部数据
    ArrayList<DailyCost> dataYear = new ArrayList<>();  //本年全部数据

    private DataBaseHelper mDataBaseHelper;
    private HorizontalScrollView mHorizontalScrollView;
    Calendar calendar = Calendar.getInstance();

    private final int VIEW_NUM = 5;

    private LinearLayout linearLayout[] = new LinearLayout[VIEW_NUM];

    private RecyclerView mRecyclerView[] = new RecyclerView[VIEW_NUM];
    private RecyclerView.Adapter mAdapter[] = new RecyclerView.Adapter[VIEW_NUM];
    private RecyclerView.LayoutManager mLayoutManager[] = new RecyclerView.LayoutManager[VIEW_NUM];

    //private ItemTouchHelper helper;

    private TextView total;
    private TextView totalIn;
    private TextView totalOut;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageView userHead;
    private Menu navMenu;

    private Double totalAccount = 0.0;
    private Double totalInAccount = 0.0;
    private Double totalOutAccount = 0.0;

    private FloatingActionButton fab;
    private View navHeader;

    private Bitmap head;// 头像Bitmap
    private static String path = "/sdcard/myHead/";// sd路径
    private Uri imageUri;

    int mOldScrollX = 0;
    int mCurScrollX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=23){  //版本号判断（以下功能只在21及以上版本实现）
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);   //活动的布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT);   //将状态栏设置为透明色
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//将状态栏字体设为黑色，该功能在23及以上版本实现
        }

        linearLayout[0] = findViewById(R.id.today_layout);
        linearLayout[1] = findViewById(R.id.week_layout);
        linearLayout[2] = findViewById(R.id.month_layout);
        linearLayout[3] = findViewById(R.id.year_layout);
        linearLayout[4] = findViewById(R.id.all_layout);


        //获取手机的宽度
        WindowManager manager = getWindowManager();
        final DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);

        for(int i = 0; i < VIEW_NUM; i++){
            linearLayout[i].setMinimumWidth(metrics.widthPixels);
        }

        mHorizontalScrollView = findViewById(R.id.account_show);
        mHorizontalScrollView.setSmoothScrollingEnabled(false);
        //mHorizontalScrollView.setNestedScrollingEnabled(false);

        /*
        mHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mOldScrollX = mHorizontalScrollView.getScrollX();
                        break;
                    case MotionEvent.ACTION_UP:
                        mCurScrollX = mHorizontalScrollView.getScrollX();
                        if ((mCurScrollX - mOldScrollX) > 0) {
                            mHorizontalScrollView.scrollTo((mOldScrollX/metrics.widthPixels+1)*metrics.widthPixels,0);
                        } else if ((mCurScrollX - mOldScrollX) < 0) {
                            mHorizontalScrollView.scrollTo((mOldScrollX/metrics.widthPixels-1)*metrics.widthPixels, 0);
                        } else {
                            mHorizontalScrollView.scrollTo((mOldScrollX/metrics.widthPixels)*metrics.widthPixels, 0);
                        }
                        break;
                }
                return false;
            }
        });
         */

        if(Build.VERSION.SDK_INT>=23){
            mHorizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollX > oldScrollX) {
                        //mHorizontalScrollView.smoothScrollTo((oldScrollX/metrics.widthPixels+1)*metrics.widthPixels,0);
                        //mHorizontalScrollView.setScrollX((oldScrollX/metrics.widthPixels+1)*metrics.widthPixels);
                        nextView((oldScrollX/metrics.widthPixels+1)*metrics.widthPixels);
                    }else if (scrollX < oldScrollX && oldScrollX != 0) {
                        //mHorizontalScrollView.smoothScrollTo((oldScrollX/metrics.widthPixels-1)*metrics.widthPixels,0);
                       // mHorizontalScrollView.setScrollX((oldScrollX/metrics.widthPixels-1)*metrics.widthPixels);
                        nextView((oldScrollX/metrics.widthPixels-1)*metrics.widthPixels);
                    }
                }

                public void nextView(int viewNext){
                    mHorizontalScrollView.setScrollX((viewNext));
                }
            });
        }

        initData();//初始化数据
        initView();//初始化页面
        clickListenerSet();//设置响应事件
    }

    //初始化数据
    private void initData() {
        mDataBaseHelper = new DataBaseHelper(this);

        //获取drawerLayout和navigationView的实例
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        fab = findViewById(R.id.fab);          //添加一条记录按钮

        //获取navView的header对象，实现点击头像跳转页面
        navHeader = navView.getHeaderView(0);
        userHead = navHeader.findViewById(R.id.user_img);

        //获取菜单
        navMenu = navView.getMenu();

        updateTotal();

        mAdapter[0] = new MyAdapter(dataDate);
        mAdapter[1] = new MyAdapter(dataWeek);
        mAdapter[2] = new MyAdapter(dataMonth);
        mAdapter[3] = new MyAdapter(dataYear);
        mAdapter[4] = new MyAdapter(dataAll);


        //deleteAndMoveList();
    }

    //初始化账单列表
    private void initView() {
        mRecyclerView[0] = findViewById(R.id.cost_view_date);
        mRecyclerView[1] = findViewById(R.id.cost_view_week);
        mRecyclerView[2] = findViewById(R.id.cost_view_month);
        mRecyclerView[3] = findViewById(R.id.cost_view_year);
        mRecyclerView[4] = findViewById(R.id.cost_view);

        for(int i = 0; i < VIEW_NUM; i++){
            //这个得在前面哈哈哈哈哈
            mLayoutManager[i] = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            // 设置布局管理器
            mRecyclerView[i].setLayoutManager(mLayoutManager[i]);
            // 设置adapter
            mRecyclerView[i].setAdapter(mAdapter[i]);
            //helper.attachToRecyclerView(mRecyclerView[i]);

            //设置系统默认动画
            mRecyclerView[i].setItemAnimator(new DefaultItemAnimator());
            mRecyclerView[i].setNestedScrollingEnabled(false);


            //mRecyclerView[i].addItemDecoration();
        }
    }

    //增加一条记录后进行刷新
    public void updateAfterAddOne(){
        updateTotal();        //从sql中重新请求数据
        mAdapter[0].notifyDataSetChanged();        //更新recyclerView
        mAdapter[1].notifyDataSetChanged();        //更新recyclerView
        mAdapter[2].notifyDataSetChanged();        //更新recyclerView
        mAdapter[3].notifyDataSetChanged();        //更新recyclerView
        mAdapter[4].notifyDataSetChanged();        //更新recyclerView
    }

    //搜索数据库
    public void updateTotal(){
        clearAll();     //清除所有数据
        queryMysql();   //重新查找数据库

        total = findViewById(R.id.test_content);
        String totalString = Double.toString(totalAccount);

        if((totalString.indexOf('.')+3)<(totalString.length()-1))
            total.setText(totalString.substring(0,totalString.indexOf('.')+3));
        else
            total.setText(totalString);

        //如果是支出
        totalOut = findViewById(R.id.total_out);
        String totalOutString = Double.toString(totalOutAccount);

        if((totalOutString.indexOf('.')+3)<(totalOutString.length()-1))
            totalOut.setText(totalOutString.substring(0,totalString.indexOf('.')+3));
        else
            totalOut.setText(totalOutString);

        totalIn = findViewById(R.id.total_in);
        String totalInString = Double.toString(totalInAccount);

        if((totalInString.indexOf('.')+3)<(totalInString.length()-1))
            totalIn.setText(totalInString.substring(0,totalString.indexOf('.')+3));
        else
            totalIn.setText(totalInString);
    }

    //清除主页面的内容
    public void clearAll(){
        //清除原data中的内容
        dataAll.clear();
        dataDate.clear();
        dataWeek.clear();
        dataMonth.clear();
        dataYear.clear();
        totalAccount = 0.0;
        totalInAccount = 0.0;
        totalOutAccount = 0.0;
    }

    //查找数据库
    public void queryMysql(){
        Cursor cursor = mDataBaseHelper.getAllCostData();
        if(cursor != null){
            while(cursor.moveToNext()){
                DailyCost tmpDaily = new DailyCost(R.drawable.duihao);
                tmpDaily.setName(cursor.getString(cursor.getColumnIndex("cost_type")));
                tmpDaily.setCost(cursor.getString(cursor.getColumnIndex("cost_money")));
                tmpDaily.setDate(cursor.getString(cursor.getColumnIndex("cost_date")));
                tmpDaily.setTime(cursor.getString(cursor.getColumnIndex("cost_time")));
                tmpDaily.setYear(cursor.getString(cursor.getColumnIndex("cost_year")));
                dataAll.add(tmpDaily);

                int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int dayInMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);
                int monthInYear = calendar.get(Calendar.MONTH);

                if(tmpDaily.getDate().substring(0,2).contains(""+(monthInYear+1))){
                    dataMonth.add(tmpDaily);
                    if(tmpDaily.getDate().substring(3).contains(""+dayInMonth)){
                        dataDate.add(tmpDaily);
                    }
                }


                Calendar c = Calendar.getInstance();
                if(monthInYear > 0){
                    c.set(calendar.get(Calendar.YEAR), monthInYear, 0); //输入类型为int类型
                }
                int days = c.get(Calendar.DAY_OF_MONTH);
                int daysInTwelve = 31;

                if(tmpDaily.getYear().equals(""+year)){
                    dataYear.add(tmpDaily);
                }

                for(int i = 0; i < dayInWeek; i++){
                    if(dayInMonth-i > 0){
                        if(tmpDaily.getDate().substring(0,2).contains(""+(monthInYear+1)) && tmpDaily.getDate().substring(3).contains(""+(dayInMonth-i))){
                            dataWeek.add(tmpDaily);
                            break;
                        }
                    }else if(monthInYear > 0){
                        if(tmpDaily.getDate().substring(0,2).contains(""+(monthInYear)) && tmpDaily.getDate().substring(3).contains(""+(days--))){
                            dataWeek.add(tmpDaily);
                            break;
                        }
                    }else{
                        if(tmpDaily.getYear().equals(""+(monthInYear-1)) && tmpDaily.getDate().substring(0,2).contains(""+12) && tmpDaily.getDate().substring(3).contains(""+(daysInTwelve--))){
                            dataWeek.add(tmpDaily);
                            break;
                        }
                    }
                }

                if(!tmpDaily.getCost().equals("")){
                    totalAccount += Double.parseDouble(tmpDaily.getCost());
                    if(tmpDaily.getCost().charAt(0) == '-')
                        totalOutAccount += Double.parseDouble(tmpDaily.getCost());
                    else
                        totalInAccount += Double.parseDouble(tmpDaily.getCost());
                }
            }
        }
        cursor.close();
    }

    public void clickListenerSet(){
        //设置响应事件
        fab.setOnClickListener(mButtonListener);
        //caidanButton.setOnClickListener(mButtonListener);
        userHead.setOnClickListener(mButtonListener);

        //设置item响应事件
        navMenu.findItem(R.id.nav_login).setOnMenuItemClickListener(mListener);
        navMenu.findItem(R.id.nav_register).setOnMenuItemClickListener(mListener);
        navMenu.findItem(R.id.nav_logout).setOnMenuItemClickListener(mListener);
        navMenu.findItem(R.id.nav_charts).setOnMenuItemClickListener(mListener);
    }

    //button响应事件
    View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fab:
                    Intent intent = new Intent(MainActivity.this, WriteDown.class);
                    startActivityForResult(intent,1);
                    break;

                case R.id.user_img:
                    showTypeDialog();
                    break;
            }
        }
    };

    //点击菜单中的不同item的不同的响应事件
    MenuItem.OnMenuItemClickListener mListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_login:
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_register:
                    Intent intent1 = new Intent(MainActivity.this, Register.class);
                    startActivity(intent1);
                    return true;
                case R.id.nav_logout:
                    Intent intent2 = new Intent(MainActivity.this, Register.class);
                    startActivity(intent2);
                    return true;
                case R.id.nav_charts:
                    Intent intent3 = new Intent(MainActivity.this, Charts.class);
                    startActivity(intent3);
                    return true;
            }
            return false;
        }
    };

    //页面信息相互传递
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentdata){
        super.onActivityResult(requestCode, resultCode, intentdata);

        //从编辑页面传送信息到主页面，如果resultCode为2就刷新列表
        if(requestCode == 1 && resultCode == 2){
            updateAfterAddOne();
        }else if(requestCode == 2 && resultCode == RESULT_OK){
            File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
            cropPhoto(Uri.fromFile(temp));// 裁剪图片
        }else if(requestCode == 3){
            if (intentdata != null) {
                Bundle extras = intentdata.getExtras();
                head = extras.getParcelable("data");
                if (head != null) {
                    /**
                     * 上传服务器代码
                     */
                    setPicToView(head);// 保存在SD卡中
                    userHead.setImageBitmap(head);// 用ImageButton显示出来
                }
            }
        }else if(requestCode == 4 && resultCode == RESULT_OK){
            cropPhoto(intentdata.getData());// 裁剪图片
        }
    }


    public  void showTypeDialog() {
        //显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera =  view.findViewById(R.id.tv_select_camera);

        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                //打开文件
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 4);
                dialog.dismiss();
            }
        });

        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);

    }

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
