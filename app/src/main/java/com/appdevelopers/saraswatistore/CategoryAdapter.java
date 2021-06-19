package com.appdevelopers.saraswatistore;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static com.appdevelopers.saraswatistore.DBqueries.lists;
import static com.appdevelopers.saraswatistore.DBqueries.loadedCategoriesNames;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categoryModelList;
    private int lastPosition = -1;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public CategoryAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String icon = categoryModelList.get(position).getCategory_icon_link();
        String name = categoryModelList.get(position).getCategory_name();
        holder.setCategory(name, position);
        holder.setCategoryIcon(icon);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryIcon;
        private TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);

        }

        private void setCategoryIcon(String iconUrl) {
            if (!iconUrl.equals("null")) {
                Glide.with(itemView.getContext()).load(iconUrl).apply(new RequestOptions().placeholder(R.drawable.avon_logo_2)).dontAnimate().into(categoryIcon);
            } else {
                categoryIcon.setImageResource(R.drawable.category_home);
            }
        }

        private void setCategory(final String name, final int position) {
            categoryName.setText(name);

            if (!name.equals("null")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != 0) {
                            Intent categoryIntent = new Intent(itemView.getContext(), CategoryActivity.class);
                            categoryIntent.putExtra("CategoryName", name);
                            itemView.getContext().startActivity(categoryIntent);
                        }
                        if (position == 0) {
                            //////refresh layout
                            HomeFragment.refreshLayout.setRefreshing(true);
                            reloadPage();
                            //////refresh layout
                        }
                    }
                });
            }

        }

        private void reloadPage() {
            connectivityManager = (ConnectivityManager) itemView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
        /*categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();*/
            DBqueries.clearData();

            if (networkInfo != null && networkInfo.isConnected() == true) {
                MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                HomeFragment.noInternetConnection.setVisibility(View.GONE);
                HomeFragment.retryBtn.setVisibility(View.GONE);
                HomeFragment.categoryRecycleView.setVisibility(View.VISIBLE);
                HomeFragment.mainRecycleView.setVisibility(View.VISIBLE);

                DBqueries.loadCategories(HomeFragment.categoryRecycleView, itemView.getContext());

                HomeFragment.categoryAdapter = new CategoryAdapter(HomeFragment.categoryModelFakeList);
                HomeFragment.categoryRecycleView.setAdapter(HomeFragment.categoryAdapter);

                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                DBqueries.loadFragmentData(HomeFragment.mainRecycleView, itemView.getContext(), 0, "Home",true);


                //homePageAdapter = new HomePageAdapter(homePageModelFakeList);
                //mainRecycleView.setAdapter(homePageAdapter);

            } else {
                MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                Toast.makeText(itemView.getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                HomeFragment.categoryRecycleView.setVisibility(View.GONE);
                HomeFragment.mainRecycleView.setVisibility(View.GONE);
                Glide.with(itemView.getContext()).load(R.drawable.no_internet_connection).into(HomeFragment.noInternetConnection);
                HomeFragment.noInternetConnection.setVisibility(View.VISIBLE);
                HomeFragment.retryBtn.setVisibility(View.VISIBLE);
                HomeFragment.refreshLayout.setRefreshing(false);
            }
        }
    }
}

