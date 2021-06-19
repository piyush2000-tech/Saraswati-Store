package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MyWishlistFragment extends Fragment {

    private RecyclerView wishlistRecycleView;
    private Dialog loadingDialog;
    public static WishListAdapter wishListAdapter;

    public MyWishlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);
        wishlistRecycleView = view.findViewById(R.id.my_wishlist_recycleview);

        ////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////loading dialog

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        wishlistRecycleView.setLayoutManager(layoutManager);

        /*List<WishListModel> wishListModelList = new ArrayList<>();
        wishListModelList.add(new WishListModel("https://firebasestorage.googleapis.com/v0/b/avonfarmer.appspot.com/o/products%2Ffruits%2Fapple.jpg?alt=media&token=4e941d4a-1162-498c-a7cb-9dfe64460a96","Banana","5",45,"90","120",true));
        wishListModelList.add(new WishListModel("https://firebasestorage.googleapis.com/v0/b/avonfarmer.appspot.com/o/products%2Ffruits%2Fapple.jpg?alt=media&token=4e941d4a-1162-498c-a7cb-9dfe64460a96","Banana","5",45,"90","120",false));

        wishListModelList.add(new WishListModel(R.drawable.banana,"Banana","5",45,"Rs. 90/-","Rs. 120/-","Cash on Delivery available"));
        wishListModelList.add(new WishListModel(R.drawable.apple,"Apple","4",28,"Rs. 120/-","Rs. 150/-","Cash on Delivery available"));
        wishListModelList.add(new WishListModel(R.drawable.cauliflower,"CauliFlower","4",45,"Rs. 90/-","Rs. 120/-","Cash on Delivery available"));
        wishListModelList.add(new WishListModel(R.drawable.pineapple,"PineApple","4.5",45,"Rs. 90/-","Rs. 120/-","Cash on Delivery not available"));
        wishListModelList.add(new WishListModel(R.drawable.banana,"Banana","5",45,"Rs. 90/-","Rs. 120/-","Cash on Delivery available"));
        wishListModelList.add(new WishListModel(R.drawable.apple,"Apple","4",45,"Rs. 90/-","Rs. 120/-","Cash on Delivery available"));*/

        if(DBqueries.wishListModelList.size() == 0){
            DBqueries.wishList.clear();
            DBqueries.loadWishList(getContext(),loadingDialog,true);
        }else{
            loadingDialog.dismiss();
        }
        wishListAdapter = new WishListAdapter(DBqueries.wishListModelList,true);
        wishlistRecycleView.setAdapter(wishListAdapter);
        wishListAdapter.notifyDataSetChanged();

        return view;
    }
}