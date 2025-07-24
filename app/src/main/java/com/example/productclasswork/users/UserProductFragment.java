package com.example.productclasswork.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.Product;

import java.util.List;

public class UserProductFragment extends Fragment {
    RecyclerView recyclerView;
    SearchView searchView;
    Spinner spinnerSort;
    UserProductAdapter adapter;
    DbHelper db;
    private int userId;
    private String username;

    public UserProductFragment(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_product, container, false);

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.userRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        spinnerSort = view.findViewById(R.id.spinnerSort);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = new DbHelper(getActivity());

        // Lấy tất cả sản phẩm từ DB
        List<Product> products = db.getAllProducts();
        adapter = new UserProductAdapter(getActivity(), products, userId, username);
        recyclerView.setAdapter(adapter);

        // Tìm kiếm sản phẩm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        // Thiết lập dữ liệu cho Spinner sắp xếp
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                new String[]{"Giá tăng dần", "Giá giảm dần"}
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        // Xử lý sự kiện chọn sắp xếp
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                List<Product> sortedProducts;
                if (position == 0) {
                    sortedProducts = db.getAllProductsSortedByPriceAsc();
                } else {
                    sortedProducts = db.getAllProductsSortedByPriceDesc();
                }
                adapter.updateData(sortedProducts);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        return view;
    }
}
