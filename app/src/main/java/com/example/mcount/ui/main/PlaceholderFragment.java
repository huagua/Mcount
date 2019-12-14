package com.example.mcount.ui.main;

import android.content.Intent;
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
        final TimePicker timePicker = root.findViewById(R.id.pick_time);
        final DatePicker datePicker = root.findViewById(R.id.pick_date);

        final int position = getArguments().getInt(ARG_SECTION_NUMBER);

        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DailyCost dailyCost = new DailyCost(R.drawable.duihao);

                if(inputMoney != null)
                    if(position == 1){
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

                //获取writeDown的实例然后调用返回方法
                WriteDown writeDown = (WriteDown)getActivity();
                writeDown.goBackMain();
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