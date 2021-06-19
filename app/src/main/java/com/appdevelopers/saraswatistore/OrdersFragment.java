package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RecoverySystem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {

    private RecyclerView OrderRecycleView;
    private OrdersAdapter ordersAdapter;
    private Dialog loadingDialog;


    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDialog.show();
        ////loading dialog

        OrderRecycleView = view.findViewById(R.id.orders_recycle_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        OrderRecycleView.setLayoutManager(linearLayoutManager);

        ordersAdapter = new OrdersAdapter(DBqueries.OrderItemModelList, loadingDialog);
        OrderRecycleView.setAdapter(ordersAdapter);

        DBqueries.orders(getContext(), ordersAdapter, loadingDialog);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ordersAdapter.notifyDataSetChanged();
    }
}