package com.appdevelopers.saraswatistore;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    public static boolean disableCloseBtn = false;
    EditText name,email,mobile,password,confpassword;
    TextView login;
    TextView closeBtn;
    Button register;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    String userId;

    String emailPattern = "[a-zA-z0-9._]+@[a-z]+.[a-z]+";


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        mobile = view.findViewById(R.id.Mobile);
        password = view.findViewById(R.id.password);
        confpassword = view.findViewById(R.id.Confirmpassword);
        register = view.findViewById(R.id.btnSignUp);
        progressBar = view.findViewById(R.id.progress);
        login = view.findViewById(R.id.login);
        closeBtn = view.findViewById(R.id.close_btn);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((AppCompatActivity)getActivity()).getSupportActionBar().show();
                //((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03A9F4")));
                setFragment(new SignInFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        if (disableCloseBtn){
            closeBtn.setVisibility(View.GONE);
        }else{
            closeBtn.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void setFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right)
                .replace(R.id.RegisterFrameLayout,fragment,fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Drawable customErrorIcon = getResources().getDrawable(R.drawable.ic_customerror_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        if(fAuth.getCurrentUser()!=null){
            mainIntent();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = name.getText().toString();
                final String userEmail = email.getText().toString();
                final String userMobile = mobile.getText().toString();
                String userPassword = password.getText().toString();
                String userReEnterPassword = confpassword.getText().toString();
                if (userName.isEmpty()) {
                    name.setError("Enter Your Full Name",customErrorIcon);
                    name.requestFocus();
                } else if (userEmail.trim().isEmpty()) {
                    email.setError("Enter Email ID",customErrorIcon);
                    email.requestFocus();
                } else if (!userEmail.trim().matches(emailPattern)) {
                    email.setError("Enter Valid Email ID",customErrorIcon);
                    email.requestFocus();
                }else if (userMobile.trim().isEmpty()) {
                    mobile.setError("Enter Mobile No.",customErrorIcon);
                    mobile.requestFocus();
                } else if (userMobile.length() != 10) {
                    mobile.setError("Enter 10 digit mobile no.",customErrorIcon);
                } else if (userPassword.trim().isEmpty()) {
                    password.setError("Enter Your Password!");
                    password.requestFocus();
                } else if (userReEnterPassword.trim().isEmpty()) {
                    confpassword.setError("Re-Enter Your Password",customErrorIcon);
                    confpassword.requestFocus();
                } else if (userPassword.length() < 6) {
                    password.setError("Password must be greater or equals to 6 characters",customErrorIcon);
                } else if (!userReEnterPassword.equals(userPassword)) {
                    confpassword.setError("Password must be same",customErrorIcon);
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    fAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "User Created Successfully...", Toast.LENGTH_SHORT).show();
                                Map<String,Object> userdata = new HashMap<>();
                                userdata.put("full_name",userName);
                                userdata.put("user_email",userEmail);

                                firestore.collection("USERS").document(fAuth.getUid())
                                        .set(userdata)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    CollectionReference userDataReference = firestore.collection("USERS").document(fAuth.getUid())
                                                            .collection("USER_DATA");
                                                    ////MAPS
                                                    Map<String,Object> wishlistMap = new HashMap<>();
                                                    wishlistMap.put("list_size", (long) 0);

                                                    Map<String,Object> ratingsMap = new HashMap<>();
                                                    ratingsMap.put("list_size", (long) 0);

                                                    Map<String,Object> cartMap = new HashMap<>();
                                                    cartMap.put("list_size", (long) 0);

                                                    Map<String,Object> myAddressesMap = new HashMap<>();
                                                    myAddressesMap.put("list_size", (long) 0);
                                                    ////MAPS

                                                    final List<String> documentNames = new ArrayList<>();
                                                    documentNames.add("MY_WISHLIST");
                                                    documentNames.add("MY_RATINGS");
                                                    documentNames.add("MY_CART");
                                                    documentNames.add("MY_ADDRESSES");

                                                    List<Map<String,Object>> documentFields = new ArrayList<>();
                                                    documentFields.add(wishlistMap);
                                                    documentFields.add(ratingsMap);
                                                    documentFields.add(cartMap);
                                                    documentFields.add(myAddressesMap);

                                                    for(int x = 0 ; x < documentNames.size();x++){
                                                        final int finalX = x;
                                                        userDataReference.document(documentNames.get(x))
                                                                .set(documentFields.get(x))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            if (finalX == documentNames.size()-1) {
                                                                                mainIntent();
                                                                            }
                                                                        }else{
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }else{
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                /*userId = fAuth.getCurrentUser().getEmail();
                                DocumentReference documentReference = firestore.collection("users").document(userId);
                                Map<String,Object> user = new HashMap<>();
                                user.put("userName",userName);
                                user.put("email",userEmail);
                                user.put("mobileno",userMobile);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "User data store successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                                //mainIntent();
                            }else{
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void mainIntent(){
        if(disableCloseBtn){
            disableCloseBtn = false;
        }else {
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }
}
