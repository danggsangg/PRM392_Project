package com.example.productclasswork.users;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.productclasswork.R;
import com.example.productclasswork.models.CartItem;
import com.example.productclasswork.models.Product;

import java.util.ArrayList;
import java.util.List;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private List<Product> fullList;
    private int userId;
    private String username;

    public UserProductAdapter(Context ctx, List<Product> list, int userId, String username) {
        this.context = ctx;
        this.productList = new ArrayList<>(list);
        this.fullList = new ArrayList<>(list);
        this.userId = userId;
        this.username = username;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtTitle, txtPrice, txtStock, txtDescription;
        ImageButton btnAddToCart;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStock = itemView.findViewById(R.id.txtStock);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product_user, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.txtTitle.setText(p.title);
        holder.txtPrice.setText("Price: $" + p.price);
        holder.txtStock.setText("Stock: " + p.stock);
        holder.txtDescription.setText(p.description);
        Glide.with(context).load(p.image).into(holder.imgProduct);
        
        // Thêm sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", p.id);
            intent.putExtra("userId", userId);
            intent.putExtra("username", username);
            context.startActivity(intent);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.addToCart(new CartItem(p.id, p.title, p.price, 1, p.stock));
            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filter(String keyword) {
        productList.clear();
        if (keyword.isEmpty()) {
            productList.addAll(fullList);
        } else {
            for (Product p : fullList) {
                if (p.title.toLowerCase().contains(keyword.toLowerCase())) {
                    productList.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setData(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        fullList.clear();
        fullList.addAll(newList);
        notifyDataSetChanged();
    }
    public void updateData(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);

        fullList.clear();
        fullList.addAll(newList);

        notifyDataSetChanged();
    }

}
