package com.example.productclasswork.admins;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.Order;
import com.example.productclasswork.models.OrderItem;
import java.util.List;

public class RevenueActivity extends AppCompatActivity {
    TextView tvTotalRevenue;
    LinearLayout layoutOrdersContainer;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        tvTotalRevenue = findViewById(R.id.txtTotalRevenue);
        layoutOrdersContainer = findViewById(R.id.layoutRevenue);
        db = new DbHelper(this);

        double totalRevenue = db.getTotalRevenue();
        tvTotalRevenue.setText("💰 Total Revenue: $" + String.format("%.2f", totalRevenue));

        List<Order> completedOrders = db.getCompletedOrders();

        if (completedOrders.isEmpty()) {
            // Empty state
            TextView emptyState = createEmptyStateView();
            layoutOrdersContainer.addView(emptyState);
        } else {
            for (Order order : completedOrders) {
                double orderTotal = 0;
                for (OrderItem item : order.items) {
                    orderTotal += item.unitPrice * item.quantity;
                }

                // Create order card
                CardView orderCard = createOrderCard(order, orderTotal);
                layoutOrdersContainer.addView(orderCard);

                // Add margin between cards
                LinearLayout.LayoutParams cardParams = (LinearLayout.LayoutParams) orderCard.getLayoutParams();
                cardParams.setMargins(0, 0, 0, dpToPx(12));
                orderCard.setLayoutParams(cardParams);
            }
        }
    }

    private CardView createOrderCard(Order order, double orderTotal) {
        // Create CardView
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(dpToPx(4));
        cardView.setRadius(dpToPx(12));
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setUseCompatPadding(true);

        // Create main container
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Order header with status badge
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView orderHeader = new TextView(this);
        orderHeader.setText("🛍️ Order #" + order.id);
        orderHeader.setTextSize(18f);
        orderHeader.setTextColor(Color.parseColor("#1A1A1A"));
        orderHeader.setTypeface(null, Typeface.BOLD);

        TextView statusBadge = new TextView(this);
        statusBadge.setText("✓ " + order.status);
        statusBadge.setTextSize(12f);
        statusBadge.setTextColor(Color.WHITE);
        statusBadge.setBackgroundColor(Color.parseColor("#4CAF50"));
        statusBadge.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        statusBadge.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        statusParams.setMargins(dpToPx(12), 0, 0, 0);
        statusBadge.setLayoutParams(statusParams);

        headerLayout.addView(orderHeader);
        headerLayout.addView(statusBadge);

        // Order total
        TextView totalText = new TextView(this);
        totalText.setText("💵 Total: $" + String.format("%.2f", orderTotal));
        totalText.setTextSize(16f);
        totalText.setTextColor(Color.parseColor("#2196F3"));
        totalText.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams totalParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        totalParams.setMargins(0, dpToPx(8), 0, dpToPx(12));
        totalText.setLayoutParams(totalParams);

        // Divider
        TextView divider = new TextView(this);
        divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
        divider.setHeight(dpToPx(1));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(1)
        );
        dividerParams.setMargins(0, 0, 0, dpToPx(12));
        divider.setLayoutParams(dividerParams);

        // Items header
        TextView itemsHeader = new TextView(this);
        itemsHeader.setText("📦 Order Items:");
        itemsHeader.setTextSize(14f);
        itemsHeader.setTextColor(Color.parseColor("#424242"));
        itemsHeader.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams itemsHeaderParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        itemsHeaderParams.setMargins(0, 0, 0, dpToPx(8));
        itemsHeader.setLayoutParams(itemsHeaderParams);

        // Add all views to main container
        mainContainer.addView(headerLayout);
        mainContainer.addView(totalText);
        mainContainer.addView(divider);
        mainContainer.addView(itemsHeader);

        // Add order items
        for (OrderItem item : order.items) {
            String productTitle = db.getProductTitleById(item.productId);

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setGravity(Gravity.CENTER_VERTICAL);
            itemLayout.setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6));
            itemLayout.setBackgroundColor(Color.parseColor("#F8F9FA"));

            LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            itemLayoutParams.setMargins(0, 0, 0, dpToPx(4));
            itemLayout.setLayoutParams(itemLayoutParams);

            TextView itemText = new TextView(this);
            itemText.setText("• " + productTitle);
            itemText.setTextSize(14f);
            itemText.setTextColor(Color.parseColor("#1A1A1A"));
            itemText.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            TextView itemDetails = new TextView(this);
            itemDetails.setText("Qty: " + item.quantity + " × $" + String.format("%.2f", item.unitPrice));
            itemDetails.setTextSize(12f);
            itemDetails.setTextColor(Color.parseColor("#666666"));
            itemDetails.setGravity(Gravity.END);

            itemLayout.addView(itemText);
            itemLayout.addView(itemDetails);
            mainContainer.addView(itemLayout);
        }

        cardView.addView(mainContainer);
        return cardView;
    }

    private TextView createEmptyStateView() {
        TextView emptyState = new TextView(this);
        emptyState.setText("📊 No completed orders yet\n\nOrders will appear here once they are completed.");
        emptyState.setTextSize(16f);
        emptyState.setTextColor(Color.parseColor("#666666"));
        emptyState.setGravity(Gravity.CENTER);
        emptyState.setPadding(dpToPx(32), dpToPx(64), dpToPx(32), dpToPx(64));
        emptyState.setBackgroundColor(Color.parseColor("#F8F9FA"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        emptyState.setLayoutParams(params);

        return emptyState;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}