package com.example.productclasswork.admins.users;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.productclasswork.R;

import java.util.List;

public class RevenueDetailAdapter extends BaseAdapter {
    private Context context;
    private List<String> orderDisplayList; // Mỗi phần tử là chuỗi hiển thị của 1 order

    public RevenueDetailAdapter(Context context, List<String> orderDisplayList) {
        this.context = context;
        this.orderDisplayList = orderDisplayList;
    }

    @Override
    public int getCount() {
        return orderDisplayList.size();
    }

    @Override
    public Object getItem(int i) {
        return orderDisplayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_revenue_detail, parent, false);
        }

        TextView txtInfo = convertView.findViewById(R.id.tvRevenueItem);
        txtInfo.setText(orderDisplayList.get(i));

        return convertView;
    }
}
