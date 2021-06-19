package com.appdevelopers.saraswatistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryActivity extends AppCompatActivity {

    private String TAG = "DeliveryActivity";

    Toolbar toolbar;
    private ConstraintLayout orderConfirmationLayout;
    private RecyclerView delivery_recycleview;
    public static CartAdapter cartAdapter;
    private Button changeOrAddNewAddressBtn;
    private TextView totalAmount;
    private TextView fullName;
    private String name, mobileNo;
    private TextView fullAddress;
    private TextView pinCode;
    private Button continueBtn;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageView paytm, cod;
    private ImageView continueShoppingBtn;
    private TextView orderId;
    private boolean successResponse = false;
    public static boolean codOrderConfirmed = false;
    private String paymentMethod = "PAYTM";

    private FirebaseFirestore firebaseFirestore;
    //public static boolean allProductsAvailable;
    public static boolean getQtyIDs = true;

    public static boolean fromCart;

    ////order Status message
    String userID ,orderID ,orderProductID , orderProductTitle ,orderProductQuantity ,orderProductPrice ,orderProductOrderDate ,orderStatus ,orderPaymentMethod ,orderAddress,orderFullName, orderPinCode;
    String orderTotalItems,orderTotalItemsPrice,orderDeliveryPrice,orderTotalAmount,orderTotalSavedAmount,orderPaymentStatus;
    ////order Status message
    private String responseData;

    private Map<String, Object> orderDetails1;
    private Map<String, Object> orderDetails2;

    public static List<CartItemModel> cartItemModelList;

    ////paytm attributes
    String M_id;
    String customer_id;
    String order_id;
    String url;
    String host;
    String callBackUrl;
    ////paytm attributes

    public static final int SELECT_ADDRESS = 0;

    private Integer ActivityRequestCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toolbar = findViewById(R.id.toolbar);
        setUpToolBar();

        setTitle("Delivery");

        delivery_recycleview = findViewById(R.id.delivery_recycleview);
        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);
        fullName = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pinCode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.contine_shopping_btn);
        orderId = findViewById(R.id.order_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;
        //allProductsAvailable = true;

        ////loading dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////loading dialog

        ////paymentMethod dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethodDialog.findViewById(R.id.paytm);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        ////paymentMethod dialog
        order_id = UUID.randomUUID().toString().substring(0, 28);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        delivery_recycleview.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        delivery_recycleview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (allProductsAvailable) {
                    paymentMethodDialog.show();
                } else {
                    ////
                }*/
                if (Integer.parseInt(totalAmount.getText().toString().trim().substring(3,totalAmount.getText().length()-2)) >= 100) {
                    Boolean allProductsAvailable = true;
                    for (CartItemModel cartItemModel : cartItemModelList) {
                        if (cartItemModel.isQtyError()) {
                            allProductsAvailable = false;
                        }
                    }
                    if (allProductsAvailable) {
                        //paytm.setEnabled(false);
                        paymentMethodDialog.show();
                    } else {
                        ////
                    }
                }else {
                    Toast.makeText(DeliveryActivity.this, "Minimum order amount should be Rs. 100 to place order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD";
                placeOrderDetails();
            }
        });
        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeliveryActivity.this, "Sorry! paytm facility currently not available.", Toast.LENGTH_SHORT).show();
                /*paymentMethod = "PAYTM";
                placeOrderDetails();*/
            }
        });

    }

    /*private void getToken() {
        //Log.e(TAG, " get token start");
        //loadingDialog.show();totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2)
        ServiceWrapper serviceWrapper = new ServiceWrapper(null);
        Call<Token_Res> call = serviceWrapper.getTokenCall("12345", M_id, order_id, totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
        call.enqueue(new Callback<Token_Res>() {
            @Override
            public void onResponse(Call<Token_Res> call, Response<Token_Res> response) {
                //Log.e(TAG, " respo "+ response.isSuccessful() );
                //loadingDialog.dismiss();
                try {

                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getBody().getTxnToken() != "") {
                            //Log.e(TAG, " transaction token : "+response.body().getBody().getTxnToken());
                            startPaytmPayment(response.body().getBody().getTxnToken());
                        } else {
                            Toast.makeText(DeliveryActivity.this, "Token status false", Toast.LENGTH_SHORT).show();
                            //Log.e(TAG, " Token status false");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, " error in Token Res " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<Token_Res> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                //Log.e(TAG, " response error "+t.toString());
                Toast.makeText(DeliveryActivity.this, "response error " + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }*/

    /*public void startPaytmPayment(String token) {
        Log.e(TAG, token);
        Log.e(TAG, " callback URL " + callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(order_id, M_id, token, totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2), callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle bundle) {
                //Toast.makeText(getApplicationContext(), "Payment Transaction response " + bundle.toString(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Response (onTransactionResponse) : " + bundle.toString());

                if (bundle.getString("STATUS").equals("TXN_SUCCESS")) {
                    Toast.makeText(DeliveryActivity.this, "Transection Successfull", Toast.LENGTH_SHORT).show();
                    if (MainActivity.mainActivity != null) {
                        MainActivity.mainActivity.finish();
                        MainActivity.mainActivity = null;
                        MainActivity.showCart = false;
                    } else {
                        MainActivity.resetMainActivity = true;
                    }

                    if (ProductDetailsActivity.productDetailsActivity != null) {
                        ProductDetailsActivity.productDetailsActivity.finish();
                        ProductDetailsActivity.productDetailsActivity = null;
                    }
                    orderId.setText("Order ID: " + bundle.getString("ORDERID"));
                    orderConfirmationLayout.setVisibility(View.VISIBLE);
                    continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(DeliveryActivity.this, "Not showing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                Toast.makeText(DeliveryActivity.this, "network not available", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "network not available ");

            }

            @Override
            public void onErrorProceed(String s) {
                Toast.makeText(DeliveryActivity.this, "onErrorProcess" + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, " onErrorProcess " + s.toString());
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Toast.makeText(DeliveryActivity.this, "Clientauth " + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Clientauth " + s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Toast.makeText(DeliveryActivity.this, " UI error " + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, " UI error " + s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Toast.makeText(DeliveryActivity.this, " error loading web " + s + "--" + s1, Toast.LENGTH_SHORT).show();
                Log.e(TAG, " error loading web " + s + "--" + s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Toast.makeText(DeliveryActivity.this, "backPress", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Toast.makeText(DeliveryActivity.this, " transaction cancel " + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, " transaction cancel " + s);
            }
        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.setAppInvokeEnabled(true);
        transactionManager.startTransaction(DeliveryActivity.this, ActivityRequestCode);

    }*/

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            Bundle bundle = data.getExtras();
            Log.e(TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " Paytm " + data.getStringExtra("STATUS"));
            Log.e(TAG, " data response - " + data.getStringExtra("response"));
            if (data.getStringExtra("response").contains("TXN_SUCCESS")) {
                Map<String, Object> updateStatus = new HashMap<>();
                updateStatus.put("Payment Status", "Paid");
                updateStatus.put("Order Status", "Paytm Ordered");
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .update(updateStatus)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> userOrder = new HashMap<>();
                                    userOrder.put("order_id", order_id);
                                    userOrder.put("time", FieldValue.serverTimestamp());
                                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("MY_ORDERS")
                                            .document(order_id)
                                            .set(userOrder)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showConfirmationLayout();
                                                    } else {
                                                        Toast.makeText(DeliveryActivity.this, "Failed to update user's order list!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(DeliveryActivity.this, "Order Cancelled!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                Toast.makeText(DeliveryActivity.this, "JAVA & ANDROID" + data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
            } else {
                for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                    if (!successResponse) {
                        for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                            final int finalX = x;
                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            cartItemModelList.get(finalX).getQtyIDs().remove(qtyID);
                                            cartAdapter.notifyDataSetChanged();
                                            finish();
                                        }
                                    });

                        }
                    } else {
                        cartItemModelList.get(x).getQtyIDs().clear();
                    }
                }
                Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + " to payment failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, " payment failed");
        }
    }*/

    public void setUpToolBar() {
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        /*if (qtyID.equals(DeliveryActivity.cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))){
                                            DeliveryActivity.cartItemModelList.get(finalX).getQtyIDs().clear();
                                        }*/
                                        cartItemModelList.get(finalX).getQtyIDs().remove(qtyID);
                                        cartAdapter.notifyDataSetChanged();
                                    }
                                });

                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ////accessing quantity
        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);
                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName)
                            .set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                        if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {

                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID())
                                                    .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                    .limit(cartItemModelList.get(finalX).getStockQuantity())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> serverQuantity = new ArrayList<>();

                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }

                                                                long availableQty = 0;
                                                                boolean noLongerAvialable = true;
                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {
                                                                    cartItemModelList.get(finalX).setQtyError(false);
                                                                    if (!serverQuantity.contains(qtyId)) {
                                                                        if (noLongerAvialable) {
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        } else {
                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "Sorry! all proucts may not be available in required quantity", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        //allProductsAvailable = false;
                                                                    } else {
                                                                        availableQty++;
                                                                        noLongerAvialable = false;
                                                                    }
                                                                }
                                                                cartAdapter.notifyDataSetChanged();
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });

                                        }
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        } else {
            getQtyIDs = true;
        }
        ////accessing quantity

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFullName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        fullName.setText(name + " - " + mobileNo);
        fullAddress.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAddress());
        pinCode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPinCode());

        ////order data
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                orderID = order_id;
                orderProductID =  cartItemModel.getProductID();
                orderProductTitle =  cartItemModel.getProductTitle();
                orderProductQuantity = ""+cartItemModel.getProductQuantity();
                orderProductPrice = cartItemModel.getProductPrice();
                orderAddress = fullAddress.getText().toString();
                orderFullName = fullName.getText().toString();
                orderPinCode = pinCode.getText().toString();


            } else {
                orderTotalItems = "Total Items"+cartItemModel.getTotalItems();
                orderTotalItemsPrice = "Total Items Price" + cartItemModel.getTotalItemPrice();
                orderDeliveryPrice = "Delivery Price" + cartItemModel.getDeliveryPrice();
                orderTotalAmount = ""+cartItemModel.getTotalAmount();
            }
        }
        ////order data

        if (codOrderConfirmed) {
            showConfirmationLayout();
            orderDetailsMessage();
        }
    }

    private void showConfirmationLayout() {
        successResponse = true;
        codOrderConfirmed = false;

        getQtyIDs = false;
        for (int x = 0; x < cartItemModelList.size() - 1; x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
            }
        }

        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
            MainActivity.showCart = false;
        }

        if (ProductDetailsActivity.productDetailsActivity != null) {
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }
        ////deliver message
        String SMS_API = "https://www.fast2sms.com/dev/bulkV2";
        final String confirmation = "Thanks for shopping with us! Your order is confirmed and will be shipped shortly. Your Order ID is here:";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(DeliveryActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(DeliveryActivity.this, "failed", Toast.LENGTH_SHORT).show();
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
                body.put("message", confirmation + " " + order_id);
                body.put("language", "english");
                body.put("route", "v3");
                body.put("numbers", mobileNo);
                body.put("flash", "0");
                return body;
            }
        };

        stringRequest.setShouldCache(false);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);
        ////deliver message


        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartlist = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();

            /*for (int x = 0; x < DBqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCartlist.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }
            }*/
            updateCartlist.put("list_size", cartListSize);
            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartlist)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DBqueries.cartList.clear();
                                DBqueries.cartItemModelList.clear();
                                /*for (int x = 0; x < indexList.size() - 1; x++) {
                                    try {
                                        DBqueries.cartList.remove(indexList.get(x).intValue());
                                        DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                                        DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                                    }catch (Exception e){
                                        Log.e("TAG",e.getMessage());
                                    }
                                }*/
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
        }
        orderId.setText("Order ID: " + order_id);
        continueBtn.setEnabled(false);
        changeOrAddNewAddressBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void placeOrderDetails() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        loadingDialog.show();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID", order_id);
                orderDetails.put("Product ID", cartItemModel.getProductID());
                orderDetails.put("Product Name", cartItemModel.getProductTitle());
                orderDetails.put("Product Image", cartItemModel.getProductImage());
                orderDetails.put("User ID", userID);
                orderDetails.put("Product quantity", cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice().trim().length() != 0) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                } else {
                    orderDetails.put("Cutted Price", "");
                }
                orderDetails.put("Product Price", cartItemModel.getProductPrice());
                /*orderDetails.put("Ordered date", FieldValue.serverTimestamp());
                orderDetails.put("Packed date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped date", FieldValue.serverTimestamp());
                orderDetails.put("Delivered date", FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date", FieldValue.serverTimestamp());*/
                orderDetails.put("Order Status", "Ordered");
                orderDetails.put("Payment Method", paymentMethod);
                orderDetails.put("Address", fullAddress.getText());
                orderDetails.put("Full Name", fullName.getText());
                orderDetails.put("Pin code", pinCode.getText());
                //orderDetails.put("Cancellation requested", false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items", cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price", cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount", cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount", cartItemModel.getSaveAmount());
                orderDetails.put("Payment Status", "not paid");
                orderDetails.put("Order Status", "Cancelled");
                orderDetails.put("Time", FieldValue.serverTimestamp());
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (paymentMethod.equals("PAYTM")) {
                                        paytm();
                                    } else {
                                        cod();
                                    }
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void paytm() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();

        M_id = "kyCXPM65329188600896";
        customer_id = FirebaseAuth.getInstance().getUid();
        url = "";
        host = "https://securegw-stage.paytm.in/";
        callBackUrl = host + "theia/paytmCallback?ORDER_ID=" + order_id;
        //callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        //getToken();
    }

    private void cod() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this, OtpVerificationActivity.class);
        otpIntent.putExtra("mobileNo", mobileNo.substring(0, 10));
        otpIntent.putExtra("orderId", order_id);
        otpIntent.putExtra("Address", fullAddress.getText());
        otpIntent.putExtra("Full Name", fullName.getText());
        otpIntent.putExtra("Pin code", pinCode.getText());
        startActivity(otpIntent);
    }

    private void orderDetailsMessage(){
        ////deliver message
        String SMS_API = "https://www.fast2sms.com/dev/bulkV2";
        //final String confirmation = "Thanks for shopping with us! Your order is confirmed and will be shipped shortly. Your Order ID is here:";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(DeliveryActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "rDNuSKcyPhndB1QXUl4G7E3WY0zqFLpJVioxCwvkfs52etHaZOd3YjgbGAv9aPoT0hBIJuS8rzxQf1yX");
                return headers;
                //wdUJgbyL5nMBzvxN9Pjimr7fl3tqH4WCTXR8Dsoc0GhIk6peK1V9NaoSYdvpXyT5e10UIbGL2JOntEAu
                //qn2jBxY5CDqddnDbcolKu36KMwAsZn9cNaXWz93Ak534CCfoQ5U0a0c5jr6r
                //tixgfn5pSmyz3MYOD4qhwlcNJb1o2LRGTKXk76HBvQZVWUCFasRSZPGyiJtA6UWVfHwMhF4ebXaov53B// avon
                //headers.put("cache-control","no-cache");
                //headers.put("content-type","application/json");
                //headers.put("accept","*/*");
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("sender_id", "TXTIND");
                body.put("message","Details : "+"Order Id : "+order_id +"  User : "+userID);////"  Pin Code : "+orderPinCode+"  Total Amount : "+orderTotalAmount
                body.put("language", "english");
                body.put("route", "v3");
                body.put("numbers", "7982723420");     ////9511575664
                body.put("flash", "0");
                return body;
            }
        };

        stringRequest.setShouldCache(false);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);
        ////deliver message
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}