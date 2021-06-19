package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderItemsDetailAdapter extends RecyclerView.Adapter<OrderItemsDetailAdapter.ViewHolder>{

    private List<MyOrderItemModel> myOrderItemModelList;
    private Dialog loadingDialog;

    public OrderItemsDetailAdapter(List<MyOrderItemModel> myOrderItemModelList, Dialog loadingDialog) {
        this.myOrderItemModelList = myOrderItemModelList;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @NotNull
    @Override
    public OrderItemsDetailAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items_layout, parent, false);
        return new OrderItemsDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderItemsDetailAdapter.ViewHolder holder, int position) {
        String resource = myOrderItemModelList.get(position).getProductImage();
        Long quantity = myOrderItemModelList.get(position).getProductQuantity();
        String title = myOrderItemModelList.get(position).getProductTitle();
        String cuttedPrice = myOrderItemModelList.get(position).getCuttedPrice();
        String price = myOrderItemModelList.get(position).getProductPrice();

        holder.setData(resource,String.valueOf(quantity),title,cuttedPrice,price);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productQuantity;
        private TextView productTitle;
        private TextView productPriceText;
        private TextView productCuttedPriceText;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productTitle = itemView.findViewById(R.id.product_title);
            productPriceText = itemView.findViewById(R.id.product_price);
            productCuttedPriceText = itemView.findViewById(R.id.cutted_price);
        }

        private void setData(final String resource,final String qty,final String title,final String cutted,final String price) {
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            productTitle.setText(title);
            productQuantity.setText("Qty : "+qty);
            productPriceText.setText("Rs. "+price+"/-");
            productCuttedPriceText.setText("Rs. "+cutted+"/-");
        }
    }
}
