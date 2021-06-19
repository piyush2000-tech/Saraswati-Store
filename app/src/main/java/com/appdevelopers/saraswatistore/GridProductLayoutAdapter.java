package com.appdevelopers.saraswatistore;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class GridProductLayoutAdapter extends RecyclerView.Adapter<GridProductLayoutAdapter.GridProductViewHolder>{

    List<GridProductModel> gridProductModelList;

    public GridProductLayoutAdapter(List<GridProductModel> gridProductModelList) {
        this.gridProductModelList = gridProductModelList;
    }

    @NonNull
    @Override
    public GridProductLayoutAdapter.GridProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_item_layout,parent,false);
        return new GridProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridProductLayoutAdapter.GridProductViewHolder holder, int position) {
        String productID = gridProductModelList.get(position).getProductID();
        String image = gridProductModelList.get(position).getGridProductImage();
        String title = gridProductModelList.get(position).getGridProductTitle();
        String description = gridProductModelList.get(position).getGridProductDescription();
        String price = gridProductModelList.get(position).getGridProductPrice();
        String cuttedPrice = gridProductModelList.get(position).getGridProductCuttedPrice();
        holder.setGridProductLayoutData(productID,image,title,description,price,cuttedPrice);
    }

    @Override
    public int getItemCount() {
        return gridProductModelList.size();
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productTitle;
        TextView productName;
        TextView productPrice;
        TextView productCuttedPrice;
        TextView productDiscountText;
        //TextView productNewText;

        public GridProductViewHolder(@NonNull final View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.grid_product_image);
            productTitle = itemView.findViewById(R.id.grid_product_item_type);
            productName = itemView.findViewById(R.id.grid_product_item_name);
            productPrice = itemView.findViewById(R.id.grid_product_item_price);
            productCuttedPrice = itemView.findViewById(R.id.grid_product_item_cutted_price);
            productDiscountText = itemView.findViewById(R.id.grid_product_discount_text);
            //productNewText = itemView.findViewById(R.id.grid_product_new_text);
        }

        private void setGridProductLayoutData(final String productid, String productImage1, String productTitle1, String productName1, String productPrice1,String productCuttedPrice1){
            Glide.with(itemView.getContext()).load(productImage1).apply(new RequestOptions().placeholder(R.drawable.avon_logo_2)).dontAnimate().into(productImage);
            productTitle.setText(productTitle1);
            productName.setText(productName1);
            productPrice.setText("Rs. "+productPrice1+"/-");
            productCuttedPrice.setText("Rs. "+productCuttedPrice1+"/-");
            /*String original = productCuttedPrice1.toString();
            String dis = productPrice1.toString();
            int originalPrice = Integer.parseInt(original);
            int discountPrice = Integer.parseInt(dis);*/
            //int discount = Integer.parseInt(productCuttedPrice1.toString())-Integer.parseInt(productPrice1.toString());
            float a = Float.parseFloat(productCuttedPrice1.toString())-Float.parseFloat(productPrice1.toString());
            float b = a / Float.parseFloat(productCuttedPrice1.toString());
            int discount = (int) (100 * b);
            productDiscountText.setText(discount+" % OFF");


            if(!productTitle1.equals("null")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productsDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productsDetailsIntent.putExtra("PRODUCT_ID",productid);
                        itemView.getContext().startActivity(productsDetailsIntent);
                    }
                });
            }
        }

    }

}
