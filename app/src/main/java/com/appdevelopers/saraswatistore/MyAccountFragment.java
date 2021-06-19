package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    public static final int MANAGE_ADDRESS = 1;
    private Button viewAllAddressBtn, signOutBtn;
    private Dialog loadingDialog;

    private CircleImageView profileView;
    private TextView name, email;
    private TextView addressName, address, pincode;


    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        viewAllAddressBtn = view.findViewById(R.id.view_all_addresses_btn);
        profileView = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        addressName = view.findViewById(R.id.address_full_name);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.address_pincode);
        signOutBtn = view.findViewById(R.id.sign_out_btn);

        ////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////loading dialog

        name.setText(DBqueries.fullName);
        email.setText(DBqueries.email);
        /*if (!DBqueries.profile.equals("")){
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions()).placeholder(R.drawable.ic_person_add_white).into(profileView);
        }*/

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingDialog.setOnDismissListener(null);
                if (DBqueries.addressesModelList.size() == 0) {
                    addressName.setText("No address");
                    address.setText("-");
                    pincode.setText("-");
                } else {
                    String nametext, mobileNo;
                    nametext = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFullName();
                    mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
                    addressName.setText(nametext + " - " + mobileNo);
                    address.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAddress());
                    pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPinCode());

                }
            }
        });

        DBqueries.loadAddress(getContext(), loadingDialog, false);

        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressesIntent = new Intent(getContext(), MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignIn.getClient(getContext(), new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build())
                        .signOut()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                /*startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                finish();*/
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
                DBqueries.clearData();
                //MainActivity.currentFragment = -1;
                startActivity(new Intent(getContext(), RegisterActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }
}