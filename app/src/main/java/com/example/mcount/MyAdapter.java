package com.example.mcount;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<DailyCost> mDailyCost;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View costView;
        ImageView typeImage;
        TextView typeView;
        TextView moneyView;

        public ViewHolder(View view){
            super(view);
            costView = view;
            typeImage = view.findViewById(R.id.ic_type);
            typeView = view.findViewById(R.id.item_type);
            moneyView = view.findViewById(R.id.item_cost);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cost_item,parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.costView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                DailyCost cost = mDailyCost.get(position);
                Toast.makeText(v.getContext(),"你点击了View"+cost.getName(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyCost cost = mDailyCost.get(position);
        holder.typeImage.setImageResource(cost.getImageId());
        holder.typeView.setText(cost.getName());
        holder.moneyView.setText(cost.getCost());
    }

    @Override
    public int getItemCount() {
        return mDailyCost.size();
    }

    public MyAdapter(List<DailyCost> CostList) {
        mDailyCost = CostList;
    }

}