package com.appdevelopers.saraswatistore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.appdevelopers.saraswatistore.DBqueries.lists;
import static com.appdevelopers.saraswatistore.DBqueries.loadFragmentData;
import static com.appdevelopers.saraswatistore.DBqueries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecycleView;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private HomePageAdapter homePageAdapter;
    private Toolbar toolbar;
    public static Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String title = getIntent().getStringExtra("CategoryName");
        categoryRecycleView = findViewById(R.id.categoryActivityRecycleView);
        toolbar = findViewById(R.id.toolbar);

        setUpToolBar();

        setTitle(title);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        ////home page fake list
        List<SliderModel> sliderModelFakeList = new ArrayList<SliderModel>();
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));

        List<GridProductModel> gridProductModelFakeList = new ArrayList<>();

        gridProductModelFakeList.add(new GridProductModel("null","null","","","100","40"));
        gridProductModelFakeList.add(new GridProductModel("null","null","","","100","40"));
        gridProductModelFakeList.add(new GridProductModel("null","null","","","100","40"));
        gridProductModelFakeList.add(new GridProductModel("null","null","","","100","40"));
        gridProductModelFakeList.add(new GridProductModel("null","null","","","100","40"));
        gridProductModelFakeList.add(new GridProductModel("null","null","","","100","40"));
        gridProductModelFakeList.add(new GridProductModel("null","null","","","100","40"));


        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"","#ffffff",gridProductModelFakeList));
        ////home page fake list





        ////////////////////

        LinearLayoutManager mainLinearLayoutManager = new LinearLayoutManager(this);
        mainLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        categoryRecycleView.setLayoutManager(mainLinearLayoutManager);

        homePageAdapter = new HomePageAdapter(homePageModelFakeList);


        int listPosition = 0;
        for(int x = 0; x<loadedCategoriesNames.size();x++){
            if(loadedCategoriesNames.get(x).equals(title.toUpperCase())){
                listPosition = x;
            }
        }
        if (listPosition == 0){
            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(categoryRecycleView,this,loadedCategoriesNames.size() - 1,title,false);
        }else{
            loadingDialog.dismiss();
            homePageAdapter = new HomePageAdapter(lists.get(listPosition));
        }
        categoryRecycleView.setAdapter(homePageAdapter);
        homePageAdapter.notifyDataSetChanged();
        ///////////////////
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent searchIntent = new Intent(this,SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}