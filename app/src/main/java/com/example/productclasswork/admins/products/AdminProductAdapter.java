package com.example.productclasswork.admins.products;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.productclasswork.DbHelper;
import com.example.productclasswork.models.Product;
import com.example.productclasswork.R;

import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private List<Product> fullList;
    public AdminProductAdapter(Context ctx, List<Product> list) {
        this.context = ctx;
        this.productList = new ArrayList<>(list);
        this.fullList = new ArrayList<>(list);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        ImageButton btnEdit, btnDelete;
        TextView txtTitle, txtPrice, txtStock, txtDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStock = itemView.findViewById(R.id.txtStock);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Product p = productList.get(pos);
        holder.txtTitle.setText(p.title);
        holder.txtPrice.setText("Price: $" + p.price);
        holder.txtStock.setText("Stock: " + p.stock);
        holder.txtDescription.setText(p.description);
        Glide.with(context).load(p.image).into(holder.imgProduct);

        holder.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, EditProductActivity.class);
            i.putExtra("product", p);
            context.startActivity(i);
            if (context instanceof Activity) {
                ((Activity) context).setResult(Activity.RESULT_OK);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Delete this product?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DbHelper db = new DbHelper(context);
                        db.deleteProduct(p.id);
                        productList.remove(pos);
                        notifyItemRemoved(pos);
                    })
                    .setNegativeButton("No", null)
                    .show();
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
    public void setProductList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

}
