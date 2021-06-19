package com.appdevelopers.saraswatistore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.widget.FrameLayout;

public class RegisterActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    Fragment fragment;

    public static boolean onResetPasswordFragment = false;
    public static boolean setSignUpFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        frameLayout = findViewById(R.id.RegisterFrameLayout);

        getSupportActionBar().hide();

        if(setSignUpFragment){
            setSignUpFragment = false;
            setDefaultFragment(new SignUpFragment());
        }else {
            setDefaultFragment(new SignInFragment());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            SignInFragment.disableCloseBtn = false;
            SignUpFragment.disableCloseBtn = false;
            if(onResetPasswordFragment){
                onResetPasswordFragment = false;
                setFragment(new SignInFragment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setDefaultFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.RegisterFrameLayout,fragment,fragment.getClass().getSimpleName())
                .commit();
    }
    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left)
                .replace(R.id.RegisterFrameLayout,fragment,fragment.getClass().getSimpleName())
                .commit();
    }
}