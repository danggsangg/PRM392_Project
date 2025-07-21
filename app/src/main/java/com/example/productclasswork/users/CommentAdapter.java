package com.example.productclasswork.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.R;
import com.example.productclasswork.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtDate, txtContent;
        RatingBar ratingBar;

        public CommentViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtCommentUsername);
            txtDate = itemView.findViewById(R.id.txtCommentDate);
            txtContent = itemView.findViewById(R.id.txtCommentContent);
            ratingBar = itemView.findViewById(R.id.ratingBarComment);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.txtUsername.setText(comment.username);
        holder.txtDate.setText(comment.date);
        holder.txtContent.setText(comment.content);
        holder.ratingBar.setRating(comment.rating);
        holder.ratingBar.setIsIndicator(true); // Không cho phép thay đổi rating
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public void setData(List<Comment> newComments) {
        this.comments = newComments;
        notifyDataSetChanged();
    }
} 