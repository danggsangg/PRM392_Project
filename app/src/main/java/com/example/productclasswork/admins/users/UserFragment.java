package com.example.productclasswork.admins.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.productclasswork.R;
import com.example.productclasswork.DbHelper;
import com.example.productclasswork.models.User;

import java.util.List;

public class UserFragment extends Fragment {
    DbHelper db;
    RecyclerView userRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userRecyclerView = view.findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = new DbHelper(getActivity());
        List<User> list = db.getAllUsers();

        UserAdapter adapter = new UserAdapter(getActivity(), list);
        userRecyclerView.setAdapter(adapter);

        return view;
    }
}
