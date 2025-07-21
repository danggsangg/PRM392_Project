package com.example.productclasswork.users;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.Comment;
import com.example.productclasswork.models.Product;
import com.example.productclasswork.models.User;
import com.example.productclasswork.utils.BadWordsChecker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private DbHelper db;
    private int productId;
    private int userId;
    private CommentAdapter commentAdapter;
    private LinearLayout layoutAddComment;
    private RatingBar ratingBarAdd;
    private EditText edtComment;
    private TextView txtReviewsLeft;
    private List<Integer> unreviewedOrderIds;
    private BadWordsChecker badWordsChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        badWordsChecker = new BadWordsChecker(this);
        
        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Details");

        productId = getIntent().getIntExtra("productId", -1);
        userId = getIntent().getIntExtra("userId", -1);
        
        if (productId == -1 || userId == -1) {
            finish();
            return;
        }

        db = new DbHelper(this);
        Product product = db.getProductById(productId);
        
        if (product == null) {
            finish();
            return;
        }

        // Ánh xạ views
        TextView txtTitle = findViewById(R.id.txtDetailTitle);
        TextView txtPrice = findViewById(R.id.txtDetailPrice);
        TextView txtStock = findViewById(R.id.txtDetailStock);
        TextView txtDescription = findViewById(R.id.txtDetailDescription);
        ImageView imgProduct = findViewById(R.id.imgDetailProduct);
        RatingBar ratingBarAverage = findViewById(R.id.ratingBarAverage);
        TextView txtRatingCount = findViewById(R.id.txtRatingCount);
        RecyclerView recyclerComments = findViewById(R.id.recyclerComments);
        layoutAddComment = findViewById(R.id.layoutAddComment);
        ratingBarAdd = findViewById(R.id.ratingBarAdd);
        edtComment = findViewById(R.id.edtComment);
        Button btnSubmitComment = findViewById(R.id.btnSubmitComment);

        // Hiển thị thông tin sản phẩm
        txtTitle.setText(product.title);
        txtPrice.setText(String.format("Price: $%.2f", product.price));
        txtStock.setText("Stock: " + product.stock);
        txtDescription.setText(product.description);
        // Có thể thêm code load ảnh vào imgProduct ở đây

        // Thiết lập RecyclerView cho comments
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        List<Comment> comments = db.getCommentsByProductId(productId);
        commentAdapter = new CommentAdapter(comments);
        recyclerComments.setAdapter(commentAdapter);

        // Hiển thị rating trung bình
        float avgRating = db.getAverageRating(productId);
        ratingBarAverage.setRating(avgRating);
        txtRatingCount.setText(String.format("(%d reviews)", comments.size()));

        // Kiểm tra số lần còn có thể review
        updateReviewStatus();

        // Xử lý sự kiện submit comment
        btnSubmitComment.setOnClickListener(v -> {
            float rating = ratingBarAdd.getRating();
            String content = edtComment.getText().toString().trim();
            
            if (rating == 0) {
                Toast.makeText(this, "Please rate the product", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (content.isEmpty()) {
                Toast.makeText(this, "Please write your review", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra từ ngữ không phù hợp
            if (badWordsChecker.containsBadWords(content)) {
                String badWord = badWordsChecker.findFirstBadWord(content);
                Snackbar snackbar = Snackbar.make(v, 
                    "Warning: Your review contains inappropriate word '" + badWord + "'. Please revise your review to follow community guidelines.", 
                    Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_orange_light, null));
                snackbar.show();
                return;
            }

            if (unreviewedOrderIds == null || unreviewedOrderIds.isEmpty()) {
                Toast.makeText(this, "No orders available to review", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log thông tin trước khi tạo comment
            android.util.Log.d("ReviewDebug", "Creating comment with orderId: " + unreviewedOrderIds.get(0));
            android.util.Log.d("ReviewDebug", "User info - userId: " + userId);

            // Lấy username từ database
            User user = db.getUserById(userId);
            if (user == null) {
                Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo comment mới cho đơn hàng đầu tiên chưa review
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(new Date());
            Comment comment = new Comment(0, productId, userId, unreviewedOrderIds.get(0),
                    user.username, content, rating, date);
            
            // Lưu vào database
            if (db.addComment(comment)) {
                // Reset form và cập nhật UI
                ratingBarAdd.setRating(0);
                edtComment.setText("");
                
                // Refresh comments và review status
                refreshComments();
                updateReviewStatus();
                
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Log thông tin lỗi
                android.util.Log.e("ReviewDebug", "Failed to save comment - productId: " + productId 
                    + ", userId: " + userId 
                    + ", orderId: " + unreviewedOrderIds.get(0));

                // Kiểm tra lại trạng thái đơn hàng
                String orderStatus = db.getOrderStatus(unreviewedOrderIds.get(0));
                android.util.Log.d("ReviewDebug", "Order status: " + orderStatus);

                Toast.makeText(this, 
                    "Failed to submit review. Please check if the order is still valid.", 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateReviewStatus() {
        try {
            int unreviewedCount = db.getUnreviewedPurchaseCount(userId, productId);
            unreviewedOrderIds = db.getOrderIdsForUnreviewedPurchases(userId, productId);
            TextView txtReviewStatus = findViewById(R.id.txtReviewStatus);

            android.util.Log.d("ReviewDebug", "Unreviewed count: " + unreviewedCount);
            android.util.Log.d("ReviewDebug", "Unreviewed order IDs: " + unreviewedOrderIds);

            if (unreviewedCount > 0) {
                layoutAddComment.setVisibility(View.VISIBLE);
                txtReviewStatus.setVisibility(View.VISIBLE);
                txtReviewStatus.setText("You can write " + unreviewedCount + " more review(s) for this product");
            } else {
                layoutAddComment.setVisibility(View.GONE);
                txtReviewStatus.setVisibility(View.VISIBLE);
                if (db.hasUserPurchasedProduct(userId, productId)) {
                    txtReviewStatus.setText("You have reviewed all your purchases of this product");
                } else {
                    txtReviewStatus.setText("Purchase this product to write a review");
                }
            }
        } catch (Exception e) {
            android.util.Log.e("ReviewDebug", "Error updating review status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshComments() {
        List<Comment> comments = db.getCommentsByProductId(productId);
        commentAdapter.setData(comments);
        float avgRating = db.getAverageRating(productId);
        RatingBar ratingBarAverage = findViewById(R.id.ratingBarAverage);
        TextView txtRatingCount = findViewById(R.id.txtRatingCount);
        ratingBarAverage.setRating(avgRating);
        txtRatingCount.setText(String.format("(%d reviews)", comments.size()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 