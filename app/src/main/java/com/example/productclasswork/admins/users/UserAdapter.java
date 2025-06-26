package com.example.productclasswork.admins.users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private DbHelper db;

    public UserAdapter(Context context, List<User> list) {
        this.context = context;
        this.userList = list;
        db = new DbHelper(context);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtRole, txtStatus;
        Button btnToggleActive;

        public UserViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtRole = itemView.findViewById(R.id.txtRole);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnToggleActive = itemView.findViewById(R.id.btnToggleActive);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User u = userList.get(position);
        holder.txtUsername.setText("Username: " + u.username);
        holder.txtRole.setText("Role: " + u.role);

        if (u.active) {
            holder.txtStatus.setText("Status: Active");
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.btnToggleActive.setText("Block");
            holder.btnToggleActive.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336"))); // Red
        } else {
            holder.txtStatus.setText("Status: Inactive");
            holder.txtStatus.setTextColor(Color.parseColor("#F44336")); // Red
            holder.btnToggleActive.setText("Unblock");
            holder.btnToggleActive.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
        }


        if (u.role.equals("admin")) {
            holder.btnToggleActive.setVisibility(View.GONE);
        } else {
            holder.btnToggleActive.setText(u.active ? "Lock" : "Unlock");
            holder.btnToggleActive.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm")
                        .setMessage("Do you want to " + (u.active ? "lock" : "unlock") + " this user?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            db.setUserActive(u.id, !u.active);
                            u.active = !u.active;
                            notifyItemChanged(position);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
