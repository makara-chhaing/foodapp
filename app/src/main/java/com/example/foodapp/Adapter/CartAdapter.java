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
import com.example.foodapp.Entity.Cart;
import com.example.foodapp.Entity.Food;
import com.example.foodapp.MapsActivity;
import com.example.foodapp.R;
import com.example.foodapp.Util.Util;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartAdapterViewHolder>{
    Context context;
    List<Cart> cartList;

    public CartAdapter(Context context, List<Cart> cartList){
        this.context = context;
        this.cartList = cartList;
    }
    @NonNull
    @Override
    public CartAdapter.CartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent,false);
        return new CartAdapter.CartAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartAdapterViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.name.setText(cart.getName());
        holder.quantity.setText(cart.getQuantity()+"");
        holder.price.setText("$"+cart.getPrice());

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, price;
        public CartAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name_id);
            quantity = itemView.findViewById(R.id.tv_quanity_id);
            price = itemView.findViewById(R.id.tv_price_id);
        }
    }
}
