package com.appdevelopers.saraswatistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OtpVerificationActivity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo;
    private String orderId;
    private String address;
    private String fullName;
    private String pinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        phoneNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verifyBtn);
        userNo = getIntent().getStringExtra("mobileNo");
        orderId = getIntent().getStringExtra("orderId");
        address = getIntent().getStringExtra("Address");
        fullName = getIntent().getStringExtra("Full Name");
        pinCode = getIntent().getStringExtra("Pin code");
        phoneNo.setText("Verification code has been sent to +91 " + userNo);

        otp.requestFocus();

        Random random = new Random();
        final int OTP_number = random.nextInt(999999 - 111111) + 111111;
        String SMS_API = "https://www.fast2sms.com/dev/bulkV2";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(OtpVerificationActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(OtpVerificationActivity.this, "OTP sent successfully! your number", Toast.LENGTH_SHORT).show();
                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (otp.getText().toString().equals(String.valueOf(OTP_number))) {
                            Map<String, Object> updateStatus = new HashMap<>();
                            updateStatus.put("Order Status", "COD Ordered");
                            FirebaseFirestore.getInstance().collection("ORDERS").document(orderId)
                                    .update(updateStatus)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> userOrder = new HashMap<>();
                                                userOrder.put("order_id", orderId);
                                                userOrder.put("order_status", "Ordered");
                                                userOrder.put("Address", address);
                                                userOrder.put("Full Name", fullName);
                                                userOrder.put("Pin Code", pinCode);
                                                userOrder.put("Ordered date", FieldValue.serverTimestamp());
                                                userOrder.put("Packed date", FieldValue.serverTimestamp());
                                                userOrder.put("Shipped date", FieldValue.serverTimestamp());
                                                userOrder.put("Delivered date", FieldValue.serverTimestamp());
                                                userOrder.put("Cancelled date", FieldValue.serverTimestamp());
                                                userOrder.put("Cancellation requested", false);
                                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("MY_ORDERS")
                                                        .document(orderId)
                                                        .set(userOrder)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    DeliveryActivity.codOrderConfirmed = true;
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(OtpVerificationActivity.this, "Failed to update user's order list!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(OtpVerificationActivity.this, "Order Cancelled!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(OtpVerificationActivity.this, "OTP verifaction failed!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "rDNuSKcyPhndB1QXUl4G7E3WY0zqFLpJVioxCwvkfs52etHaZOd3YjgbGAv9aPoT0hBIJuS8rzxQf1yX");
                //headers.put("cache-control","no-cache");
                //headers.put("content-type","application/json");
                //headers.put("accept","*/*");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("sender_id", "TXTIND");
                body.put("message", "Dear, customer your OTP verification code is: " + OTP_number);
                body.put("language", "english");
                body.put("route", "v3");
                body.put("numbers", userNo);
                body.put("flash", "0");
                return body;
            }
        };

        stringRequest.setShouldCache(false);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(OtpVerificationActivity.this);
        requestQueue.add(stringRequest);

        //requestQueue.getCache().clear();
    }
}