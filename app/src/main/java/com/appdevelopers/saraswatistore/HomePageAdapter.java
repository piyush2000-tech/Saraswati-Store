package com.appdevelopers.saraswatistore;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private int lastPosition = -1;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.GRID_PRODUCT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);

            case HomePageModel.GRID_PRODUCT:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridProductView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;
            case HomePageModel.GRID_PRODUCT:
                String title = homePageModelList.get(position).getTitle();
                String backColor = homePageModelList.get(position).getBackGroundColor();
                List<GridProductModel> gridProductModelList = homePageModelList.get(position).getGridProductModelList();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductModelList, title,backColor);
            default:
                return;
        }
        if(lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager bannerSliderViewPager;
        private List<SliderModel> arrangeList;
        private Timer timer;
        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;
        int currentPage;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);


        }

        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {
            currentPage = 2;
            if(timer != null){
                timer.cancel();
            }

            arrangeList = new ArrayList<>();
            for(int x=0;x<sliderModelList.size();x++){
                arrangeList.add(x,sliderModelList.get(x));
            }
            arrangeList.add(0,sliderModelList.get(sliderModelList.size() - 2));
            arrangeList.add(1,sliderModelList.get(sliderModelList.size() - 1));
            arrangeList.add(sliderModelList.get(0));
            arrangeList.add(sliderModelList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangeList);
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);
            bannerSliderViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        PageLooper(arrangeList);
                    }
                }
            };
            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideShow(arrangeList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    PageLooper(arrangeList);
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow(arrangeList);
                    }
                    return false;
                }
            });
        }

        private void PageLooper(List<SliderModel> sliderModelList) {
            if (currentPage == sliderModelList.size() - 2) {
                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }

            if (currentPage == 1) {
                currentPage = sliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
        }

        private void startBannerSlideShow(final List<SliderModel> sliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerSlideShow() {
            timer.cancel();
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder {

        private TextView gridLayoutTitle;
        private RecyclerView mainRecycleView;
        private ConstraintLayout container;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridLayoutTitle = itemView.findViewById(R.id.grid_product_layout_title);
            container = itemView.findViewById(R.id.grid_container);
            mainRecycleView = itemView.findViewById(R.id.grid_product_layout_recycleView);
            mainRecycleView.setRecycledViewPool(recycledViewPool);
        }

        private void setGridProductLayout(final List<GridProductModel> gridProductModelList, String title, String backgroundColor) {
            gridLayoutTitle.setText(title);
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(backgroundColor)));

            for (final GridProductModel model : gridProductModelList){
                if (!model.getProductID().isEmpty() && model.getGridProductTitle().isEmpty()){
                    firebaseFirestore.collection("PRODUCTS").document(model.getProductID())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        model.setGridProductTitle(task.getResult().getString("product_title"));
                                        model.setGridProductDescription(task.getResult().getString("product_type"));
                                        model.setGridProductImage(task.getResult().getString("product_image_1"));
                                        model.setGridProductPrice(task.getResult().getString("product_price"));
                                        model.setGridProductCuttedPrice(task.getResult().getString("cutted_price"));
                                        //model.setGridProductNew(task.getResult().getBoolean("product_new"));

                                        if (gridProductModelList.indexOf(model) == gridProductModelList.size()-1){
                                            if (mainRecycleView.getAdapter() != null){
                                                mainRecycleView.getAdapter().notifyDataSetChanged();
                                            }
                                        }
                                    }else {
                                        ////nothing
                                    }
                                }
                            });
                }
            }
            /*LinearLayoutManager mainLinearLayoutManager = new LinearLayoutManager(itemView.getContext());
            mainLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);*/
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 2, GridLayoutManager.VERTICAL, false);
            mainRecycleView.setLayoutManager(gridLayoutManager);
            mainRecycleView.setAdapter(new GridProductLayoutAdapter(gridProductModelList));
        }

    }
}

