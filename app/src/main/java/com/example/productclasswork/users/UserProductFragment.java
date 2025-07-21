package com.example.productclasswork.users;

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
import com.example.productclasswork.R;
import com.example.productclasswork.models.Product;

import java.util.List;

public class UserProductFragment extends Fragment {
    RecyclerView recyclerView;
    SearchView searchView;
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

        recyclerView = view.findViewById(R.id.userRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = new DbHelper(getActivity());
        List<Product> products = db.getAllProducts();
        adapter = new UserProductAdapter(getActivity(), products, userId, username);
        recyclerView.setAdapter(adapter);

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

        return view;
    }
}
