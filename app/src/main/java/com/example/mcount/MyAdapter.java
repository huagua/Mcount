package com.example.mcount;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

//RecyclerView的适配器
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<DailyCost> mDailyCost;
    private DataBaseHelper mDataBaseHelper;

    //静态内部类
    static class ViewHolder extends RecyclerView.ViewHolder{
        View costView;
        ImageView typeImage;
        TextView typeView;
        TextView moneyView;
        TextView dateView;
        TextView timeView;

        public ViewHolder(View view){
            super(view);
            costView = view;
            typeImage = view.findViewById(R.id.ic_type);
            typeView = view.findViewById(R.id.item_type);
            moneyView = view.findViewById(R.id.item_cost);
            dateView = view.findViewById(R.id.item_date);
            timeView = view.findViewById(R.id.item_time);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cost_item,parent, false);
        final ViewHolder holder = new ViewHolder(view);
        mDataBaseHelper = new DataBaseHelper(view.getContext());

        //设置点击监听事件
        holder.costView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                DailyCost cost = mDailyCost.get(position);
                setView(cost, v,position);


                //Toast.makeText(v.getContext(),"你点击了View"+cost.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.typeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                DailyCost cost = mDailyCost.get(position);
                Toast.makeText(view.getContext(), "你点击了图片"+ cost.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.moneyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                DailyCost cost = mDailyCost.get(position);
                Toast.makeText(v.getContext(),"你点击了View"+cost.getCost(), Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    public void setView(final DailyCost cost, View v, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        final AlertDialog dialog = builder.create();
        View view = View.inflate(v.getContext(), R.layout.dialog_check_account, null);
        TextView delete_account =  view.findViewById(R.id.delete_account);

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataBaseHelper.deleteCost(cost);
                mDailyCost.remove(cost);
                notifyItemRemoved(position);

                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    //动态更新值
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyCost cost = mDailyCost.get(position);
        holder.typeImage.setImageResource(cost.getImageId());
        holder.typeView.setText(cost.getName());
        holder.moneyView.setText(cost.getCost());
        holder.dateView.setText(cost.getDate());
        holder.timeView.setText(cost.getTime());
    }

    @Override
    public int getItemCount() {
        return mDailyCost.size();
    }

    //构造函数
    public MyAdapter(List<DailyCost> CostList) {
        mDailyCost = CostList;
    }

}