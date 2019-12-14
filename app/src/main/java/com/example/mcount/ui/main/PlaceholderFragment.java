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

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private DataBaseHelper mDataBase;

    private String tmpDate = "";

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

        //获取当前tab的位置，1是支出，2是收入
        final int position = getArguments().getInt(ARG_SECTION_NUMBER);

        //设置监听事件
        dateAndTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = View.inflate(getActivity(), R.layout.dialog_date_time, null);
                final TimePicker timePicker = view.findViewById(R.id.pick_time);
                final DatePicker datePicker = view.findViewById(R.id.pick_date);
                builder.setView(view);

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
                dialog.show();
            }
        });




        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DailyCost dailyCost = new DailyCost(R.drawable.duihao);

                if(inputMoney != null)
                    if(position == 1){
                        dailyCost.setCost("-"+inputMoney.getText().toString());
                    }else
                        dailyCost.setCost(inputMoney.getText().toString());

                    //    if(getActivity()!= null)
                            //Toast.makeText(getActivity().getApplicationContext(),"请输入金额！",Toast.LENGTH_SHORT).show();


                if(inputType != null)
                //if(!inputType.getText().equals(""))
                    dailyCost.setName(inputType.getText().toString());

                /*
                String tmpDate = "";
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

                 */


                tmpDate = dateAndTime.getText().toString();

                if(!dailyCost.getCost().equals("") && !dailyCost.getCost().equals("")){
                    dailyCost.setDate(tmpDate);

                    //将该条账单插入数据库中
                    mDataBase.insertCost(dailyCost);

                    //获取writeDown的实例然后调用返回方法
                    WriteDown writeDown = (WriteDown)getActivity();
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
}