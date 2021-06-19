package com.appdevelopers.saraswatistore;

import java.util.List;

public class HomePageModel {
    public static final int BANNER_SLIDER = 0;
    public static final int GRID_PRODUCT =1;

    private int type;

    ///////////// Banner Slider
    private List<SliderModel> sliderModelList;
    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }
    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }
    ///////////// Banner Slider

    //////Grid Product Layout
    private String title;
    private String backGroundColor;
    private List<GridProductModel> gridProductModelList;

    public HomePageModel(int type, String title,String backGroundColor, List<GridProductModel> gridProductModelList) {
        this.type = type;
        this.title = title;
        this.backGroundColor = backGroundColor;
        this.gridProductModelList = gridProductModelList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public List<GridProductModel> getGridProductModelList() {
        return gridProductModelList;
    }

    public void setGridProductModelList(List<GridProductModel> gridProductModelList) {
        this.gridProductModelList = gridProductModelList;
    }

    //////Grid Product Layout


}

