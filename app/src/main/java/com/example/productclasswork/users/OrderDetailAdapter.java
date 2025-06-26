package com.example.productclasswork.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.R;
import com.example.productclasswork.models.CartItem;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private final List<CartItem> items;

    public OrderDetailAdapter(List<CartItem> items) {
        this.items = items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtQty, txtPrice;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtQty = itemView.findViewById(R.id.txtQty);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.txtTitle.setText(item.title);
        holder.txtQty.setText("Qty: " + item.quantity);
        holder.txtPrice.setText("Price: $" + item.price);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
