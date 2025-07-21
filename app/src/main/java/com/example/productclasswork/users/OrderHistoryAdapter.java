package com.example.productclasswork.users;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.CartItem;
import com.example.productclasswork.models.Order;
import com.example.productclasswork.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private final List<Order> orderList;

    public OrderHistoryAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderInfo;
        Button btnCancel;
        public OrderViewHolder(View itemView) {
            super(itemView);
            txtOrderInfo = itemView.findViewById(R.id.txtOrderInfo);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        StringBuilder info = new StringBuilder("Order #" + order.id + " (" + order.date + ")\n");
        double total = 0;
        for (OrderItem item : order.items) {
            info.append("- Product ID: ").append(item.productId)
                    .append(", Qty: ").append(item.quantity)
                    .append(", $").append(item.unitPrice).append("\n");
            total += item.unitPrice * item.quantity;
        }
        info.append("Total: $").append(total);
        info.append("Status: ").append(order.status).append("\n");

        holder.txtOrderInfo.setText(info.toString());

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), OrderDetailActivity.class);
            i.putExtra("orderItems", new ArrayList<>(order.items));
            v.getContext().startActivity(i);
        });

        Button btnCancel = holder.btnCancel;
        if ("Pending".equals(order.status)) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                    .setTitle("Cancel Order")
                    .setMessage("Are you sure you want to cancel this order? You won't be able to review the products after cancellation.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DbHelper db = new DbHelper(v.getContext());
                        db.updateOrderStatus(order.id, "Cancelled");
                        order.status = "Cancelled";
                        notifyItemChanged(position);
                    })
                    .setNegativeButton("No", null)
                    .show();
            });
        } else {
            btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

