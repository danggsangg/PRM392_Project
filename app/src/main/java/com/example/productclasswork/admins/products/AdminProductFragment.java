package com.example.productclasswork.admins.products;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.models.Product;
import com.example.productclasswork.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminProductFragment extends Fragment {
    RecyclerView recyclerView;
    AdminProductAdapter adapter;
    DbHelper db;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = new DbHelper(getActivity());
        List<Product> list = db.getAllProducts();
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

        FloatingActionButton fab = view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EditProductActivity.class));
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProductList() {
        List<Product> newList = db.getAllProducts();
        adapter.setData(newList);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProductList(); // reload lại mỗi khi quay về tab
    }

}
