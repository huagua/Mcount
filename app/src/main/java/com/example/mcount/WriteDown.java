package com.example.mcount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mcount.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class WriteDown extends AppCompatActivity {

    private DataBaseHelper mDataBase;
    private EditText inputMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDataBase = new DataBaseHelper(this);
        inputMoney = findViewById(R.id.money);



        setContentView(R.layout.activity_write_down);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fabConfirm = findViewById(R.id.fab_confirm);
        FloatingActionButton fabConcel = findViewById(R.id.fab_concel);


        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();

                String tmp = "";
                for(int i = 0; i < 10; i++) {
                    DailyCost tmpdaily = new DailyCost(R.drawable.duihao);
                    tmpdaily.setDate("2012-11-24");
                    tmpdaily.setName("收入");
                    tmpdaily.setCost(tmp+i);
                    mDataBase.insertCost(tmpdaily);
                }

                //data.putExtra("data", content);

                /*
                if(inputMoney != null){
                    DailyCost dailyCost = new DailyCost(R.drawable.duihao);
                    dailyCost.setCost(inputMoney.getText().toString());
                    dailyCost.setName();//怎么知道当前是收入还是支出呢？
                }

                 */

                setResult(2, data);
                finish();
            }
        });

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