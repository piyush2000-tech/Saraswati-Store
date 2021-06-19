package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.appdevelopers.saraswatistore.DBqueries.categoryModelList;
import static com.appdevelopers.saraswatistore.DBqueries.firebaseFirestore;
import static com.appdevelopers.saraswatistore.DBqueries.lists;
import static com.appdevelopers.saraswatistore.DBqueries.loadCategories;
import static com.appdevelopers.saraswatistore.DBqueries.loadFragmentData;
import static com.appdevelopers.saraswatistore.DBqueries.loadedCategoriesNames;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public static SwipeRefreshLayout refreshLayout;
    public static RecyclerView categoryRecycleView;
    public static List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    public static CategoryAdapter categoryAdapter;
    public static RecyclerView mainRecycleView;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();

    public static ImageView noInternetConnection;
    public static Button retryBtn;

    public static Dialog loadingDialog;
    //private List<CategoryModel> categoryModelList;
    //private FirebaseFirestore firebaseFirestore;
    public static HomePageAdapter homePageAdapter;

    /*///////////// Banner Slider
    private ViewPager bannerSliderViewPager;
    private List<SliderModel> sliderModelList;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;
    int currentPage = 2;
    //////////// Banner Slider

    ///////// Grid Product Layout
    private List<GridProductModel> gridProductModelList;
    //////// Grid Product Layout*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecycleView = view.findViewById(R.id.categoryRecycleView);
        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        retryBtn = view.findViewById(R.id.retryBtn);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        mainRecycleView = view.findViewById(R.id.mainRecycleView);

        loadingDialog = new Dialog(getActivity());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        refreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.redColor),getContext().getResources().getColor(R.color.redColor),getContext().getResources().getColor(R.color.redColor));

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        //mainRecycleView.setLayoutManager(gridLayoutManager);
        categoryRecycleView.setLayoutManager(gridLayoutManager);

        final LinearLayoutManager mainLinearLayoutManager = new LinearLayoutManager(getContext());
        mainLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        mainRecycleView.setLayoutManager(mainLinearLayoutManager);

        ////categories fake list
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        ////categories fake list

        ////home page fake list
        List<SliderModel> sliderModelFakeList = new ArrayList<SliderModel>();
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));

        List<GridProductModel> gridProductModelFakeList = new ArrayList<>();

        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));
        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));
        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));
        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));
        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));
        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));
        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));
        gridProductModelFakeList.add(new GridProductModel("null","","Product Name","Product Type","100","100"));


        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"","#dfdfdf",gridProductModelFakeList));
        ////home page fake list

        //categoryModelList = new ArrayList<CategoryModel>();
        categoryAdapter = new CategoryAdapter(categoryModelFakeList);

        homePageAdapter = new HomePageAdapter(homePageModelFakeList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecycleView.setVisibility(View.VISIBLE);
            mainRecycleView.setVisibility(View.VISIBLE);
            if(categoryModelList.size() == 0){
                loadCategories(categoryRecycleView,getContext());
            }else{
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecycleView.setAdapter(categoryAdapter);
            if(lists.size() == 0){
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(mainRecycleView,getContext(),0,"Home",true);
            }else{
                loadingDialog.dismiss();
                homePageAdapter = new HomePageAdapter(lists.get(0));
                homePageAdapter.notifyDataSetChanged();
            }
            mainRecycleView.setAdapter(homePageAdapter);
        }else{
            loadingDialog.dismiss();
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecycleView.setVisibility(View.GONE);
            mainRecycleView.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.no_internet_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
        }

        //////refresh layout
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                reloadPage();

            }
        });
        //////refresh layout

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        ///////////////////
        return view;
    }

    private void reloadPage(){
        networkInfo = connectivityManager.getActiveNetworkInfo();
        /*categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();*/
        DBqueries.clearData();

        if(networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecycleView.setVisibility(View.VISIBLE);
            mainRecycleView.setVisibility(View.VISIBLE);

            loadCategories(categoryRecycleView,getContext());

            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            categoryRecycleView.setAdapter(categoryAdapter);

            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            DBqueries.loadFragmentData(mainRecycleView,getContext(),0,"Home",true);


            //homePageAdapter = new HomePageAdapter(homePageModelFakeList);
            //mainRecycleView.setAdapter(homePageAdapter);

        }else {
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            categoryRecycleView.setVisibility(View.GONE);
            mainRecycleView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.no_internet_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
        }
    }
}