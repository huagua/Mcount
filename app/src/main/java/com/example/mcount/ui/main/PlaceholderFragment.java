package com.example.mcount.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mcount.DailyCost;
import com.example.mcount.DataBaseHelper;
import com.example.mcount.R;
import com.example.mcount.WriteDown;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private DataBaseHelper mDataBase;

    private String tmpDate = "";
    private String tmpTime = "";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        mDataBase = new DataBaseHelper(getContext());

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_write_down, container, false);
        final TextView textView = root.findViewById(R.id.section_label);

        //获取fab按钮实例
        FloatingActionButton fabConfirm = root.findViewById(R.id.fab_confirm);

        final EditText inputMoney = root.findViewById(R.id.money);
        final EditText inputType = root.findViewById(R.id.type);

        final TextView dateAndTime = root.findViewById(R.id.text_date_time);

        //提供一个初始值给日期
        initTmpDate();
        dateAndTime.setText(tmpDate);

        //获取当前tab的位置，1是支出，2是收入
        final int position = getArguments().getInt(ARG_SECTION_NUMBER);

        //设置选择日期和时间按钮的监听事件
        dateAndTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = View.inflate(getActivity(), R.layout.dialog_date_time, null);
                final TimePicker timePicker = view.findViewById(R.id.pick_time);
                final DatePicker datePicker = view.findViewById(R.id.pick_date);
                builder.setView(view);

                tmpDate = "";
                builder.setTitle("选取起始时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(Build.VERSION.SDK_INT>=21){

                            String minute = "";
                            if(timePicker.getMinute() < 10){
                                minute+="0"+timePicker.getMinute();
                            }else{
                                minute+=timePicker.getMinute();
                            }

                            String date = "";
                            if(datePicker.getDayOfMonth() < 10){
                                date += " "+datePicker.getDayOfMonth();
                            }else{
                                date += datePicker.getDayOfMonth();
                            }

                            String month = "";
                            if(datePicker.getMonth()+1 < 10){
                                month += " "+(datePicker.getMonth()+1);
                            }else{
                                month += (datePicker.getMonth()+1);
                            }

                            String hour = "";
                            if(timePicker.getHour() < 10){
                                hour += " "+timePicker.getHour();
                            }else{
                                hour += timePicker.getHour();
                            }

                            tmpDate+=month+"-"+date+"  "+hour+":"+minute;
                        }

                        dateAndTime.setText(tmpDate);

                        dialog.cancel();
                    }
                });

                builder.setNegativeButton("取  消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                Dialog dialog = builder.create();
                //dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);设置dialog背景色，略丑，
                dialog.show();
            }
        });




        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取writeDown的实例然后调用返回方法
                WriteDown writeDown = (WriteDown)getActivity();

                DailyCost dailyCost = new DailyCost(R.drawable.duihao);

                //判断用户是否输入了内容
                if(inputMoney.getText().toString().equals("") || inputType.getText().toString().equals("")){

                    //如果没有输入金额就弹出"请输入金额"
                    if(inputMoney.getText().toString().equals(""))
                        writeDown.showToast(1);

                    //如果没有输入类型就弹出"请输入类型"
                    if(inputType.getText().toString().equals(""))
                        writeDown.showToast(2);
                }else{
                    //判断当前是收入还是支出
                    if(position == 1)
                        dailyCost.setCost("-"+inputMoney.getText().toString());
                    else
                        dailyCost.setCost(inputMoney.getText().toString());

                    dailyCost.setName(inputType.getText().toString());

                    tmpDate = dateAndTime.getText().toString();
                    tmpDate = tmpDate.substring(0,tmpDate.indexOf(' '));
                    tmpTime = dateAndTime.getText().toString();
                    tmpTime = tmpTime.substring(tmpTime.indexOf(' ')+1);

                    dailyCost.setDate(tmpDate);
                    dailyCost.setTime(tmpTime);

                    //将该条账单插入数据库中
                    mDataBase.insertCost(dailyCost);

                    writeDown.goBackMain();
                }
            }
        });

        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    //初始化tmpDate的值为当前时间
    public void initTmpDate(){
        tmpDate = "";
        Calendar calendar = Calendar.getInstance();

        //获取系统的日期
        //月
        int month = calendar.get(Calendar.MONTH)+1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //获取系统时间
        //小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);

        String minuteS = "";
        if(minute < 10){
            minuteS+="0"+minute;
        }else{
            minuteS+=minute;
        }

        String dayS = "";
        if(day < 10){
            dayS += " "+day;
        }else{
            dayS += day;
        }

        String monthS = "";
        if(month < 10){
            monthS += " "+month;
        }else{
            monthS += month;
        }

        String hourS = "";
        if(hour < 10){
            hourS += " "+hour;
        }else{
            hourS += hour;
        }

        tmpDate+=monthS+"-"+dayS+"  "+hourS+":"+minuteS;
    }
}