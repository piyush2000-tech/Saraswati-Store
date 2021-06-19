package com.appdevelopers.saraswatistore;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.SignInButtonImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {


    public static final int GOOGLE_SIGN_IN_CODE = 10005;
    public static boolean disableCloseBtn = false;
    EditText email,password;
    Button login;
    TextView ForgetPassword,register;
    TextView closeBtn;
    LottieAnimationView lottieAnimation;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    SignInButtonImpl signIn;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    GoogleSignInAccount signInAccount;
    String googleUserName,googleEmail,googleMobile;
    String userId;
    private Dialog loadingDialog;


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.btnSignIn);
        ForgetPassword = view.findViewById(R.id.forget);
        register = view.findViewById(R.id.register);
        lottieAnimation = view.findViewById(R.id.lottAnim);
        signIn =view.findViewById(R.id.btnSignInGoogle);
        closeBtn = view.findViewById(R.id.close_btn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        ////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////loading dialog

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(),BrainActivity.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);*/
                //((AppCompatActivity)getActivity()).getSupportActionBar().show();
                //((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03A9F4")));
                setFragment(new SignUpFragment());
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
                .setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left)
                .replace(R.id.RegisterFrameLayout,fragment,fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Drawable customErrorIcon = getResources().getDrawable(R.drawable.ic_customerror_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("973242183224-i3trlir7gt1csn90ec8mrt8bqccvglte.apps.googleusercontent.com")
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(getActivity(),gso);

        signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        if(signInAccount != null){
            //Toast.makeText(getActivity(), "User is Logged in Already", Toast.LENGTH_SHORT).show();
            mainIntent();
        }else if(fAuth.getCurrentUser()!=null){
            mainIntent();
        }

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingDialog.show();
                Intent sign = signInClient.getSignInIntent();
                startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailval = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if(TextUtils.isEmpty(emailval)){
                    email.setError("Please enter Email ID",customErrorIcon);
                    Toast.makeText(getActivity(), "Please enter Email ID", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                }else if(TextUtils.isEmpty(pass)){
                    password.setError("Please enter password",customErrorIcon);
                    password.requestFocus();
                }else{
                    lottieAnimation.setVisibility(View.VISIBLE);

                    fAuth.signInWithEmailAndPassword(emailval,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "successfully login...", Toast.LENGTH_SHORT).show();
                                mainIntent();
                                /*Intent intent = new Intent(getActivity(),MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();*/
                            }else{
                                Toast.makeText(getActivity(), "Invalid Username and Password!", Toast.LENGTH_SHORT).show();
                                lottieAnimation.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGN_IN_CODE){
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                final GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);

                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(),null);
                fAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Your Google Account Connected Successfully...", Toast.LENGTH_SHORT).show();
                        //googleUserName = fAuth.getCurrentUser().getDisplayName();
                        //googleEmail = fAuth.getCurrentUser().getEmail();
                        /*userId = fAuth.getCurrentUser().getEmail();
                        DocumentReference documentReference = fStore.collection("googleusers").document(userId);
                        Map<String,Object> googleuser = new HashMap<>();
                        googleuser.put("userName",signInAcc.getDisplayName());
                        googleuser.put("email",signInAcc.getEmail());
                        //user.put("mobileno",fAuth.getCurrentUser().getPhoneNumber());
                        documentReference.set(googleuser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(getActivity(), "User data store successfully...", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                        //intent.putExtra("gooleUserName",googleUserName);
                        //intent.putExtra("googleEmail",googleEmail);
                        startActivity(intent);
                        getActivity().finish();*/
                        loadingDialog.show();
                        DocumentReference documentReference = fStore.collection("USERS").document(fAuth.getUid());
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()){
                                        //Toast.makeText(getActivity(), "Document Already exist.....", Toast.LENGTH_SHORT).show();
                                        mainIntent();
                                    }else{
                                        //Toast.makeText(getActivity(), "Document not Already exist.....", Toast.LENGTH_SHORT).show();
                                        Map<String, Object> userdata = new HashMap<>();
                                        userdata.put("full_name", signInAcc.getDisplayName());
                                        userdata.put("user_email", signInAcc.getEmail());

                                        fStore.collection("USERS").document(fAuth.getUid())
                                                .set(userdata)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            CollectionReference userDataReference = fStore.collection("USERS").document(fAuth.getUid())
                                                                    .collection("USER_DATA");
                                                            ////MAPS
                                                            Map<String, Object> wishlistMap = new HashMap<>();
                                                            wishlistMap.put("list_size", (long) 0);

                                                            Map<String, Object> ratingsMap = new HashMap<>();
                                                            ratingsMap.put("list_size", (long) 0);

                                                            Map<String, Object> cartMap = new HashMap<>();
                                                            cartMap.put("list_size", (long) 0);

                                                            Map<String, Object> myAddressesMap = new HashMap<>();
                                                            myAddressesMap.put("list_size", (long) 0);
                                                            ////MAPS

                                                            final List<String> documentNames = new ArrayList<>();
                                                            documentNames.add("MY_WISHLIST");
                                                            documentNames.add("MY_RATINGS");
                                                            documentNames.add("MY_CART");
                                                            documentNames.add("MY_ADDRESSES");

                                                            List<Map<String, Object>> documentFields = new ArrayList<>();
                                                            documentFields.add(wishlistMap);
                                                            documentFields.add(ratingsMap);
                                                            documentFields.add(cartMap);
                                                            documentFields.add(myAddressesMap);

                                                            for (int x = 0; x < documentNames.size(); x++) {
                                                                final int finalX = x;
                                                                userDataReference.document(documentNames.get(x))
                                                                        .set(documentFields.get(x))
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    if (finalX == documentNames.size() - 1) {
                                                                                        mainIntent();
                                                                                    }
                                                                                } else {
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    loadingDialog.dismiss();
                                }else{
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
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