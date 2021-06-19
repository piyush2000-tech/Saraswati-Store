package com.appdevelopers.saraswatistore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.appdevelopers.saraswatistore.DeliveryActivity.SELECT_ADDRESS;
import static com.appdevelopers.saraswatistore.MyAccountFragment.MANAGE_ADDRESS;
import static com.appdevelopers.saraswatistore.MyAddressesActivity.refreshItem;


public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectedPosition;

    public AddressesAdapter(List<AddressesModel> addressesModelList,int MODE) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBqueries.selectedAddress;
    }

    @NonNull
    @Override
    public AddressesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.ViewHolder holder, int position) {
        String name = addressesModelList.get(position).getFullName();
        String userAddress = addressesModelList.get(position).getAddress();
        String userPincode = addressesModelList.get(position).getPinCode();
        Boolean selected = addressesModelList.get(position).getSelected();
        String mobileNo = addressesModelList.get(position).getMobileNo();
        holder.setData(name,userAddress,userPincode,selected,position,mobileNo);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullName;
        private TextView address;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout optionCntainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pincode);
            icon = itemView.findViewById(R.id.icon_view);
            optionCntainer = itemView.findViewById(R.id.option_container);
        }

        private void setData(String name, String addressText, String pincodeText, Boolean select, final int position,String mobileNo){
            fullName.setText(name+" - "+mobileNo);
            address.setText(addressText);
            pincode.setText(pincodeText);

            if(MODE == SELECT_ADDRESS){
                icon.setImageResource(R.drawable.ic_check);
                if(select){
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                }else{
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(preSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBqueries.selectedAddress = position;
                        }
                    }
                });
            }else if(MODE == MANAGE_ADDRESS){
                optionCntainer.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_vertical_dots);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionCntainer.setVisibility(View.VISIBLE);
                        refreshItem(preSelectedPosition,preSelectedPosition);
                        preSelectedPosition = position;

                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(preSelectedPosition,preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }
        }
    }
}
