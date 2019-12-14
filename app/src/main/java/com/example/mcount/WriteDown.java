package com.example.mcount;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mcount.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


/*
* 编辑新账单的页面
* */
public class WriteDown extends AppCompatActivity {

    private DataBaseHelper mDataBase;

    //定义需要的控件
    private EditText inputMoney;
    private EditText inputType;
    private TimePicker timePicker;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_down);

        mDataBase = new DataBaseHelper(this);

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        //获取viewpager
        ViewPager viewPager = findViewById(R.id.view_pager);

        //设置viewpager的适配器
        viewPager.setAdapter(sectionsPagerAdapter);

        //获取tablayout
        final TabLayout tabs = findViewById(R.id.tabs);

        //设置viewpager
        tabs.setupWithViewPager(viewPager);

        //获取两个fab按钮实例
        FloatingActionButton fabConfirm = findViewById(R.id.fab_confirm);
        FloatingActionButton fabConcel = findViewById(R.id.fab_concel);


        //设置confirm按钮监听事件，
        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                DailyCost dailyCost = new DailyCost(R.drawable.duihao);

                //获取当前tab id，是收入1？还是支出0？
                int position = tabs.getSelectedTabPosition();
                String currentTab = sectionsPagerAdapter.getPageTitle(position).toString();
                dailyCost.setName(currentTab);

                //View v = LayoutInflater.from(WriteDown.this).inflate(R.layout.fragment_write_down, null);

                //在此处获取控件id
                inputMoney = findViewById(R.id.money);
                inputType = findViewById(R.id.type);
                datePicker = findViewById(R.id.pick_date);
                timePicker = findViewById(R.id.pick_time);

                if(inputMoney != null)
                    if(position == 0){
                        dailyCost.setCost("-"+inputMoney.getText().toString());
                    }else
                        dailyCost.setCost(inputMoney.getText().toString());

                if(inputType != null)  dailyCost.setName(inputType.getText().toString());

                String tmpDate = "";
                if(Build.VERSION.SDK_INT>=21){
                    tmpDate+=datePicker.getDayOfMonth()+"/"+(datePicker.getMonth()+1)+"  "+timePicker.getHour()+":"+timePicker.getMinute();
                }
                dailyCost.setDate(tmpDate);

                //将该条账单插入数据库中
                mDataBase.insertCost(dailyCost);

                //跳回主页面，并传送code：2
                setResult(2, data);
                finish();
            }
        });

        //设置concel按钮监听事件
        fabConcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("data", "aniyoaniyo");
                setResult(3, data);
                finish();
            }
        });
    }

}