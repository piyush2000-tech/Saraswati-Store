package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MyCartFragment extends Fragment {


    private RecyclerView cartItemsRecycleView;
    private Button continueBtn;
    private Dialog loadingDialog;
    private TextView totalAmount;
    public static CartAdapter cartAdapter;
    private LinearLayout linearLayout;
    private NestedScrollView nestedScrollView;

    public static int y;

    public MyCartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        ////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////loading dialog

        cartItemsRecycleView = view.findViewById(R.id.cart_item_recycleview);
        continueBtn = view.findViewById(R.id.cart_continue_btn);
        totalAmount = view.findViewById(R.id.total_cart_amount);
        linearLayout = view.findViewById(R.id.linearLayout);
        nestedScrollView = view.findViewById(R.id.nested_scroll_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        cartItemsRecycleView.setLayoutManager(layoutManager);

        /*cartItemModelList.add(new CartItemModel(0,R.drawable.banana,"Banana","Rs. 50/-","Rs. 80/-",1,"Rs. 50.50/250 gm"));
        cartItemModelList.add(new CartItemModel(0,R.drawable.apple,"Apple","Rs. 100/-","Rs. 120/-",4,"Rs. 100/1 kg"));
        cartItemModelList.add(new CartItemModel(0,R.drawable.pineapple,"PineApple","Rs. 150/-","Rs. 180/-",2,"Rs. 50.50/250 gm"));
        cartItemModelList.add(new CartItemModel(0,R.drawable.cauliflower,"CauliFlower","Rs. 50/-","Rs. 80/-",1,"Rs. 50.50/250 gm"));
        cartItemModelList.add(new CartItemModel(0,R.drawable.banana,"Banana","Rs. 50/-","Rs. 80/-",1,"Rs. 50.50/250 gm"));*/
        /*List<CartItemModel> cartItemModelList = new ArrayList<>();
        cartItemModelList.add(new CartItemModel(1,"Price (5 items)","Rs. 400/-","Free","Rs. 400/-","Rs. 140/-"));*/

        /*if (nestedScrollView != null){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY){
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    if (scrollY < oldScrollY){
                        linearLayout.setVisibility(View.GONE);
                    }
                }
            });
        }*/
        /*cartItemsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (RecyclerView.SCROLL_STATE_IDLE == newState){
                    if (y > 0){
                        linearLayout.setVisibility(View.GONE);
                    }else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
                y = dy;

            }
        });*/
        cartAdapter = new CartAdapter(DBqueries.cartItemModelList,totalAmount,true);
        //cartAdapter.notifyItemChanged(CartAdapter.cartItemModelList.size()-1,"total_amount_layout");
        cartItemsRecycleView.setAdapter(cartAdapter);
        cartItemsRecycleView.setItemAnimator(null);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;

                for (int x=0; x < DBqueries.cartItemModelList.size(); x++){
                    CartItemModel cartItemModel = DBqueries.cartItemModelList.get(x);

                    if (cartItemModel.isInStock()){
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                loadingDialog.show();
                if (DBqueries.addressesModelList.size() == 0) {
                    DBqueries.loadAddress(getContext(), loadingDialog,true);
                }else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        cartAdapter.notifyDataSetChanged();
        LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
        parent.setVisibility(View.GONE);
        if(DBqueries.cartItemModelList.size() == 0){
            DBqueries.cartList.clear();
            DBqueries.loadCartList(getContext(),loadingDialog,true,new TextView(getContext()),totalAmount);
        }else{
            if (DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size()-1).getType() == CartItemModel.TOTAL_AMOUNT){
                //LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

    }
}