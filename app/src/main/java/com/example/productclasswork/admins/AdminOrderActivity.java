// AdminOrderActivity.java
package com.example.productclasswork.admins;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.Order;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtEmpty;
    AdminOrderAdapter adapter;

    private int currentPage = 1;
    private int pageSize = 5;
    private int totalPages;
    private List<Order> allOrders;
    private LinearLayout paginationLayout;
    Button currentPageBtn = null;
    private HorizontalScrollView paginationScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        recyclerView = findViewById(R.id.recyclerAdminOrders);
        txtEmpty = findViewById(R.id.txtEmptyAdminOrder);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DbHelper db = new DbHelper(this);

        paginationScrollView = findViewById(R.id.paginationScroll);
        paginationLayout = findViewById(R.id.paginationLayout);
        allOrders = db.getAllOrdersWithUsernames();

        totalPages = (int) Math.ceil((double) allOrders.size() / pageSize);

        if (allOrders.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            paginationLayout.setVisibility(View.GONE);
        } else {
            loadPage(currentPage);
            setupPagination(paginationLayout, paginationScrollView, currentPage, totalPages, this::onPageChanged);
        }
    }

    private void onPageChanged(int page) {
        currentPage = page;
        loadPage(currentPage);
        setupPagination(paginationLayout, paginationScrollView, currentPage, totalPages, this::onPageChanged);
    }

    private void loadPage(int page) {
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allOrders.size());
        List<Order> subList = allOrders.subList(start, end);
        adapter = new AdminOrderAdapter(subList);
        recyclerView.setAdapter(adapter);
    }

    private void setupPagination(LinearLayout paginationLayout, HorizontalScrollView paginationScroll, int currentPage, int totalPages, PageChangeListener listener) {
        paginationLayout.removeAllViews();
        Context context = paginationLayout.getContext();

        final int MAX_VISIBLE = 5;
        Button currentPageBtn = null;

        // "<"
        if (currentPage > 1) {
            Button prev = createPageButton(context, "<", -1, currentPage, () -> listener.onPageChanged(currentPage - 1));
            paginationLayout.addView(prev);
        }

        int startPage, endPage;
        if (totalPages <= MAX_VISIBLE) {
            startPage = 2;
            endPage = totalPages - 1;
        } else if (currentPage <= 3) {
            startPage = 2;
            endPage = MAX_VISIBLE - 1;
        } else if (currentPage >= totalPages - 2) {
            startPage = totalPages - (MAX_VISIBLE - 2);
            endPage = totalPages - 1;
        } else {
            startPage = currentPage - 1;
            endPage = currentPage + 1;
        }

        // "1"
        Button btnFirst = createPageButton(context, "1", 1, currentPage, () -> listener.onPageChanged(1));
        paginationLayout.addView(btnFirst);
        if (currentPage == 1) currentPageBtn = btnFirst;

        // "..."
        if (startPage > 2) {
            paginationLayout.addView(createDots(context));
        }

        for (int i = startPage; i <= endPage; i++) {
            if (i == 1 || i == totalPages) continue;
            final int pageNum = i;
            Button btn = createPageButton(context, String.valueOf(i), pageNum, currentPage, () -> listener.onPageChanged(pageNum));
            if (i == currentPage) currentPageBtn = btn;
            paginationLayout.addView(btn);
        }

        // "..."
        if (endPage < totalPages - 1) {
            paginationLayout.addView(createDots(context));
        }

        // Last
        if (totalPages > 1) {
            Button btnLast = createPageButton(context, String.valueOf(totalPages), totalPages, currentPage, () -> listener.onPageChanged(totalPages));
            paginationLayout.addView(btnLast);
            if (currentPage == totalPages) currentPageBtn = btnLast;
        }

        // ">"
        if (currentPage < totalPages) {
            Button next = createPageButton(context, ">", -1, currentPage, () -> listener.onPageChanged(currentPage + 1));
            paginationLayout.addView(next);
        }

        // Auto scroll to center
        if (currentPageBtn != null) {
            View finalCurrentPageBtn = currentPageBtn;
            paginationScroll.post(() -> {
                int x = finalCurrentPageBtn.getLeft() - paginationScroll.getWidth() / 2 + finalCurrentPageBtn.getWidth() / 2;
                paginationScroll.smoothScrollTo(Math.max(0, x), 0);
            });
        }
    }


    public interface PageChangeListener {
        void onPageChanged(int page);
    }

    private Button createPageButton(Context context, String text, int page, int currentPage, Runnable onClick) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setTextSize(14f);
        btn.setTypeface(null, Typeface.NORMAL);
        btn.setAllCaps(false);
        btn.setPadding(32, 12, 32, 12);
        btn.setTextColor(page == currentPage ? Color.WHITE : Color.BLACK);
        btn.setBackgroundResource(page == currentPage ? R.drawable.bg_page_button_selected : R.drawable.bg_page_button);

        btn.setOnClickListener(v -> onClick.run());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);
        btn.setLayoutParams(params);
        return btn;
    }


    private TextView createDots(Context context) {
        TextView dots = new TextView(context);
        dots.setText("...");
        dots.setTextSize(16f);
        dots.setTextColor(Color.GRAY);
        dots.setPadding(12, 0, 12, 0);
        return dots;
    }

}

