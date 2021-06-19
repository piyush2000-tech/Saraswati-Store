package com.appdevelopers.saraswatistore;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordFragment extends Fragment {

    private EditText registerEmail;
    private Button resetPasswordBtn;
    private TextView goBack;
    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailIconText;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        registerEmail = view.findViewById(R.id.forgot_password_email);
        resetPasswordBtn = view.findViewById(R.id.reset_password_btn);
        goBack = view.findViewById(R.id.forgot_password_go_back);

        emailIconContainer = view.findViewById(R.id.forgot_linear_layout);
        emailIcon = view.findViewById(R.id.forgot_password_email_icon);
        emailIconText = view.findViewById(R.id.forgot_password_email_icon_text);
        progressBar = view.findViewById(R.id.forgot_password_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Drawable customErrorIcon = getResources().getDrawable(R.drawable.ic_customerror_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        registerEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailIcon.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);
                resetPasswordBtn.setEnabled(true);
                resetPasswordBtn.setBackgroundColor(Color.parseColor("#EB3798E4"));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = registerEmail.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Please enter Email ID",customErrorIcon);
                    //Toast.makeText(getActivity(), "Please enter Email ID", Toast.LENGTH_SHORT).show();
                    registerEmail.requestFocus();
                }else{
                    TransitionManager.beginDelayedTransition(emailIconContainer);
                    emailIconText.setVisibility(View.GONE);

                    TransitionManager.beginDelayedTransition(emailIconContainer);
                    emailIcon.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    resetPasswordBtn.setEnabled(false);
                    resetPasswordBtn.setBackgroundColor(Color.parseColor("#70221F1F"));
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                emailIconText.setText("Recover email sent successfully! Check your Inbox.");
                                emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
                                TransitionManager.beginDelayedTransition(emailIconContainer);
                                emailIconText.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                /*ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0);
                                scaleAnimation.setDuration(100);
                                scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                scaleAnimation.setRepeatMode(Animation.REVERSE);
                                scaleAnimation.setRepeatCount(1);
                                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        emailIconText.setText("Recover email sent successfully! Check your Inbox.");
                                        emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
                                        TransitionManager.beginDelayedTransition(emailIconContainer);
                                        emailIconText.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });*/
                            }else {
                                Toast.makeText(getActivity(), "Invalid Email Id", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            resetPasswordBtn.setEnabled(true);
                            resetPasswordBtn.setBackgroundColor(Color.parseColor("#EB3798E4"));

                            emailIconText.setText(e.getMessage());
                            emailIconText.setTextColor(getResources().getColor(R.color.redColor));
                            TransitionManager.beginDelayedTransition(emailIconContainer);
                            emailIconText.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    public void setFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left)
                .replace(R.id.RegisterFrameLayout,fragment,fragment.getClass().getSimpleName())
                .commit();
    }
}