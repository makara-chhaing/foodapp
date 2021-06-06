package com.example.foodapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodapp.Databasehelper.Database;
import com.example.foodapp.Entity.Food;
import com.example.foodapp.R;
import com.example.foodapp.Util.Util;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodAdapterViewHolder> {

    Context context;
    Database database = Util.fooduser_db;
    List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foods){
        this.context = context;
        this.foodList = foods;
    }
    @NonNull
    @Override
    public FoodAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_component, parent,false);
        return new FoodAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapterViewHolder holder, int position) {
        Food food = foodList.get(position);
        Log.d("result: ", position+": position");
        Log.d("result: ", food.getName() + " :header");
        Log.d("result: ", food.getDescription() + " :description");
        holder.imageView.setImageBitmap(food.getImgBitmap());
        holder.header.setText(food.getName());
        holder.body.setText(food.getDescription());

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, food.getName());
                    intent.setType("text/plain");
                    context.startActivity(Intent.createChooser(intent, "Sharing Food..."));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class FoodAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView header, body;
        ImageButton share;
        public FoodAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.componet_iv_img);
            header = itemView.findViewById(R.id.componet_tv_header);
            body = itemView.findViewById(R.id.componet_tv_body);
            share = itemView.findViewById(R.id.iv_share);
        }
    }
}
