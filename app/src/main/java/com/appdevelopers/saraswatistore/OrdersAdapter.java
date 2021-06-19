package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<OrderItemModel> OrderItemModelList;
    private Dialog loadingDialog;

    public OrdersAdapter(List<OrderItemModel> myOrderItemModelList, Dialog loadingDialog) {
        this.OrderItemModelList = myOrderItemModelList;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        String orderId = OrderItemModelList.get(position).getOrderID();
        Date orderDate = OrderItemModelList.get(position).getOrderDate();
        String orderStatus = OrderItemModelList.get(position).getOrderStatus();
        Date packedDate = OrderItemModelList.get(position).getPackedDate();
        Date shippedDate = OrderItemModelList.get(position).getShippedDate();
        Date deliveredDate = OrderItemModelList.get(position).getDeliveredDate();
        Date cancelDate = OrderItemModelList.get(position).getCancelledDate();
        boolean cancelRequest = OrderItemModelList.get(position).isCancellationRequested();
        String address = OrderItemModelList.get(position).getAddress();
        String fullName = OrderItemModelList.get(position).getFullName();
        String pinCode = OrderItemModelList.get(position).getPinCode();
        holder.setData(orderId,orderDate,orderStatus,position,packedDate,shippedDate,deliveredDate,cancelDate,cancelRequest,address,fullName,pinCode);
    }

    @Override
    public int getItemCount() {
        return OrderItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView orderId,orderDate,orderStatus;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);
        }

        private void setData(String id,Date date,String status,int position,Date packed,Date shipped,Date delivered,Date cancelled,boolean cancel,String address,String fullNAme,String pinCode){
            orderId.setText(id);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm aa");
            orderDate.setText(String.valueOf(simpleDateFormat.format(date)));
            orderStatus.setText(status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailsIntent = new Intent(itemView.getContext(), OrderActivity.class);
                    orderDetailsIntent.putExtra("Position", position);
                    orderDetailsIntent.putExtra("OrderID",id);
                    orderDetailsIntent.putExtra("Status",status);
                    orderDetailsIntent.putExtra("OrderDate",date.toString());
                    orderDetailsIntent.putExtra("PackedDate",packed.toString());
                    orderDetailsIntent.putExtra("ShippedDate",shipped.toString());
                    orderDetailsIntent.putExtra("DeliveredDate",delivered.toString());
                    orderDetailsIntent.putExtra("CancelledDate",cancelled.toString());
                    orderDetailsIntent.putExtra("CancellationRequest",cancel);
                    itemView.getContext().startActivity(orderDetailsIntent);
                }
            });
        }
    }
}
