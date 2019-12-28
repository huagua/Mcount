package com.example.mcount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mcount.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


/*
* 编辑新账单的页面
* */
public class WriteDown extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_down);

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        //获取viewpager
        ViewPager viewPager = findViewById(R.id.view_pager);

        //设置viewpager的适配器
        viewPager.setAdapter(sectionsPagerAdapter);

        //获取tablayout
        final TabLayout tabs = findViewById(R.id.tabs);

        //设置viewpager
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fabConcel = findViewById(R.id.fab_concel);

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

        /*
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

         */

    }

    public void showToast(int code){
        if(code == 1)
            Toast.makeText(this,"请输入金额！", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"请输入类型！", Toast.LENGTH_SHORT).show();

    }


    public void goBackMain(){
        Intent data = new Intent();
        setResult(2, data);
        finish();
    }


}