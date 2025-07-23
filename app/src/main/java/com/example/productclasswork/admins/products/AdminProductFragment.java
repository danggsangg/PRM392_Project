package com.example.productclasswork.admins.products;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminProductFragment extends Fragment {
    RecyclerView recyclerView;
    AdminProductAdapter adapter;
    DbHelper db;
    private SearchView searchView;
    private Spinner spinnerSort;
    private List<Product> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = new DbHelper(getActivity());
        list = db.getAllProducts();

        adapter = new AdminProductAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

        searchView = view.findViewById(R.id.searchView);
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

        // ✅ Spinner sắp xếp
        spinnerSort = view.findViewById(R.id.spinnerSort);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item,
                new String[]{"Mặc định", "Giá tăng dần", "Giá giảm dần"}
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                    case 1: // Giá tăng dần
                        adapter.setData(db.getAllProductsSortedByPriceAsc());
                        break;
                    case 2: // Giá giảm dần
                        adapter.setData(db.getAllProductsSortedByPriceDesc());
                        break;
                    default:
                        adapter.setData(db.getAllProducts()); // reset mặc định
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        FloatingActionButton fab = view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EditProductActivity.class));
        });

        return view;
    }

    private void loadProductList() {
        List<Product> newList = db.getAllProducts();
        adapter.setData(newList);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProductList(); // reload khi quay lại
    }
}
