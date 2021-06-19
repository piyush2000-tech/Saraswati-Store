package com.appdevelopers.saraswatistore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText city;
    private EditText locality;
    private EditText flatNo;
    private EditText pinCode;
    private EditText landmark;
    private EditText name;
    private EditText mobileNo;
    private EditText alterNateMobileNo;
    private AppCompatSpinner stateSpinner;
    Toolbar toolbar;
    private Button saveBtn;
    private Dialog loadingDialog;

    private String[] stateList;
    private String selectState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        toolbar = findViewById(R.id.toolbar);
        saveBtn = findViewById(R.id.save_btn);
        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        flatNo = findViewById(R.id.flat_no);
        pinCode = findViewById(R.id.pincode);
        landmark = findViewById(R.id.landmark);
        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.mobile_no);
        alterNateMobileNo = findViewById(R.id.alternate_mobile_no);
        stateSpinner = findViewById(R.id.state_spinner);

        setUpToolBar();

        setTitle("Add a new address");

        ////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////loading dialog

        stateList = getResources().getStringArray(R.array.india_states);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSpinner.setAdapter(spinnerAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (city.getText().toString().trim().length() != 0 && city.getText().toString().trim().toLowerCase().equals("rajsamand")  || city.getText().toString().trim().toLowerCase().equals("kankroli")){
                    if (locality.getText().toString().trim().length() != 0){
                        if (flatNo.getText().toString().trim().length() != 0){
                            if (pinCode.getText().toString().trim().length() != 0 && pinCode.getText().length()==6 && pinCode.getText().toString().trim().equals("313324")){
                                if (name.getText().toString().trim().length() != 0){
                                    if (mobileNo.getText().toString().trim().length() != 0 && mobileNo.getText().length() == 10){
                                        loadingDialog.show();
                                        final String fullAddress = flatNo.getText().toString()+" "+locality.getText().toString()+" "+landmark.getText().toString()+" "+city.getText().toString()+" "+selectState;

                                        Map<String,Object> addAddress = new HashMap<>();
                                        addAddress.put("list_size",(long)DBqueries.addressesModelList.size() + 1);
                                        if (TextUtils.isEmpty(alterNateMobileNo.getText())) {
                                            addAddress.put("mobile_no_" + String.valueOf((long) DBqueries.addressesModelList.size() + 1), mobileNo.getText().toString());
                                        }else{
                                            addAddress.put("mobile_no_" + String.valueOf((long) DBqueries.addressesModelList.size() + 1), mobileNo.getText().toString()+" or "+ alterNateMobileNo.getText().toString());
                                        }
                                        addAddress.put("fullname_" + String.valueOf((long) DBqueries.addressesModelList.size() + 1), name.getText().toString());
                                        addAddress.put("address_"+String.valueOf((long)DBqueries.addressesModelList.size() + 1),fullAddress);
                                        addAddress.put("pincode_"+String.valueOf((long)DBqueries.addressesModelList.size() + 1),pinCode.getText().toString());
                                        addAddress.put("selected_"+String.valueOf((long)DBqueries.addressesModelList.size() + 1),true);

                                        if (DBqueries.addressesModelList.size() > 0) {
                                            addAddress.put("selected_" + (DBqueries.selectedAddress + 1), false);
                                        }

                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("MY_ADDRESSES")
                                                .update(addAddress)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            if (DBqueries.addressesModelList.size() > 0) {
                                                                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                                                            }
                                                            if (TextUtils.isEmpty(alterNateMobileNo.getText())) {
                                                                DBqueries.addressesModelList.add(new AddressesModel(name.getText().toString()
                                                                        , fullAddress, pinCode.getText().toString(), true,mobileNo.getText().toString()));
                                                                ;
                                                            }else{
                                                                DBqueries.addressesModelList.add(new AddressesModel(name.getText().toString()
                                                                        , fullAddress, pinCode.getText().toString(), true, mobileNo.getText().toString()+" or "+alterNateMobileNo.getText().toString()));
                                                            }

                                                            if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                                Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                                startActivity(deliveryIntent);
                                                            }else {
                                                                MyAddressesActivity.refreshItem(DBqueries.selectedAddress,DBqueries.addressesModelList.size()-1);
                                                            }
                                                            DBqueries.selectedAddress = DBqueries.addressesModelList.size() - 1;
                                                            finish();
                                                        }else{
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                        loadingDialog.dismiss();
                                                    }
                                                });
                                    }else {
                                        mobileNo.requestFocus();
                                        mobileNo.setError("Please! provide valid Mobile No.");
                                    }
                                }else{
                                    name.requestFocus();
                                    name.setError("Please! enter you name..");
                                }
                            }else{
                                pinCode.requestFocus();
                                pinCode.setError("Please! provide valid pincode.");
                                Toast.makeText(AddAddressActivity.this, "Currently! delivery available only on 313324(Rajsamand City)", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            flatNo.requestFocus();
                            flatNo.setError("Please! enter flatNo. name.");
                        }
                    }else {
                        locality.requestFocus();
                        locality.setError("Please! enter locality.");
                    }
                }else{
                    city.requestFocus();
                    city.setError("Please! enter city name kankroli or rajsamand.");
                    Toast.makeText(AddAddressActivity.this, "Currently! delivery available only in Rajsamand and kankroli", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpToolBar() {
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}