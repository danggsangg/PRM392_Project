package com.example.productclasswork.users;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.R;
import com.example.productclasswork.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private List<CartItem> selectedItems = new ArrayList<>();
    private OnTotalChangedListener totalListener;

    public interface OnTotalChangedListener {
        void onChanged(double total);
    }

    public CartAdapter(List<CartItem> list, OnTotalChangedListener listener) {
        this.cartItems = list;
        this.totalListener = listener;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPrice, txtQty;
        ImageButton btnDelete, btnIncrease, btnDecrease;
        CheckBox chkBuy;

        public CartViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQty = itemView.findViewById(R.id.txtQty);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            chkBuy = itemView.findViewById(R.id.chkBuy);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.txtTitle.setText(item.title);
        holder.txtPrice.setText("Price: $" + item.price);
        holder.txtQty.setText("Qty: " + item.quantity);

        holder.chkBuy.setChecked(selectedItems.contains(item));
        holder.chkBuy.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) selectedItems.add(item);
            else selectedItems.remove(item);
            notifyTotalChanged();
        });

        holder.btnIncrease.setOnClickListener(v -> {
            if (item.quantity < item.stock) {
                item.quantity++;
                notifyItemChanged(position);
                notifyTotalChanged();
            } else {
                Toast.makeText(v.getContext(), "Exceeds stock limit", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                notifyItemChanged(position);
                notifyTotalChanged();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Remove")
                    .setMessage("Remove this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        CartManager.removeItem(item);
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        selectedItems.remove(item);
                        notifyTotalChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void notifyTotalChanged() {
        double total = 0;
        for (CartItem i : selectedItems) {
            total += i.price * i.quantity;
        }
        totalListener.onChanged(total);
    }

    public List<CartItem> getSelectedItems() {
        return selectedItems;
    }

    public void setData(List<CartItem> newList) {
        cartItems = newList;
        selectedItems.clear();
        notifyDataSetChanged();
        notifyTotalChanged();
    }
}

