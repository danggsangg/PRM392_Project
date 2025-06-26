package com.example.productclasswork.admins;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.Order;
import com.example.productclasswork.models.OrderItem;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    private final List<Order> orderList;

    public AdminOrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderInfo, txtStatus, txtTotal;
        Button btnAccept;

        public OrderViewHolder(View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderInfo = itemView.findViewById(R.id.txtOrderInfo);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set basic info
        holder.txtOrderId.setText("Order #" + order.id);
        holder.txtOrderInfo.setText("User: " + order.username + " | Date: " + order.date);
        holder.txtStatus.setText("Status: " + order.status);

        // Set status color
        switch (order.status) {
            case "Pending":
                holder.txtStatus.setTextColor(0xFFFF9800); // Orange
                break;
            case "Delivering":
                holder.txtStatus.setTextColor(0xFF2196F3); // Blue
                break;
            case "Cancelled":
                holder.txtStatus.setTextColor(0xFF9E9E9E); // Gray
                break;
            default:
                holder.txtStatus.setTextColor(0xFF4CAF50); // Green
                break;
        }

        // Calculate total
        double total = 0;
        for (OrderItem item : order.items) {
            total += item.unitPrice * item.quantity;
        }
        holder.txtTotal.setText("Total: $" + String.format("%.2f", total));

        // Accept button logic
        if ("Pending".equals(order.status)) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnAccept.setOnClickListener(v -> {
                DbHelper db = new DbHelper(v.getContext());
                db.updateOrderStatus(order.id, "Delivering");
                order.status = "Delivering";
                notifyItemChanged(holder.getAdapterPosition());
            });
        } else {
            holder.btnAccept.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
