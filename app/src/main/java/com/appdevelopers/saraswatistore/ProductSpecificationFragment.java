package com.appdevelopers.saraswatistore;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductSpecificationFragment extends Fragment {

    private RecyclerView productSpecificationRecyclerView;
    public List<ProductSpecificationModel> productSpecificationModelList;

    public ProductSpecificationFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_product_specification, container, false);

        productSpecificationRecyclerView = view.findViewById(R.id.product_specification_recycle_view);

        //productSpecificationRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        productSpecificationRecyclerView.setLayoutManager(linearLayoutManager);
        //productSpecificationRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //productSpecificationModelList = new ArrayList<>();
        /*productSpecificationModelList.add(new ProductSpecificationModel(0,"General"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(0,"Manufactring Details"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"Quality","Fresh"));*/
        /*productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));
        productSpecificationModelList.add(new ProductSpecificationModel("Quality","Fresh"));*/



        ProductSpecificationAdapter productSpecificationAdapter = new ProductSpecificationAdapter(productSpecificationModelList);
        productSpecificationRecyclerView.setAdapter(productSpecificationAdapter);
        productSpecificationAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}