package com.appdevelopers.saraswatistore;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;
import hotchemi.android.rate.AppRate;

import static com.appdevelopers.saraswatistore.RegisterActivity.setSignUpFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static DrawerLayout drawerLayout;
    CoordinatorLayout coordinatorLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private Dialog signInDialog;
    private FirebaseUser currentUser;
    private TextView badgeCount;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public static Activity mainActivity;

    private CircleImageView profileView;
    private TextView fullName,email;

    private int count = 0;

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int WISH_FRAGMENT = 3;
    private static final int MY_ACCOUNT_FRAGMENT = 4;
    private static final int ABOUT_US = 5;

    public static Boolean showCart = false;
    public static boolean resetMainActivity = false;

    private int scrollFlags;
    private AppBarLayout.LayoutParams params;

    private FrameLayout frameLayout;
    private int currentFragment = -1;

    MenuItem previousMenuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setUpToolBar();

        setTitle(R.string.app_name);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        AppRate.with(this)
                .setInstallDays(2)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();

        profileView = navigationView.getHeaderView(0).findViewById(R.id.main_profile_img);
        fullName = navigationView.getHeaderView(0).findViewById(R.id.main_fullname);
        email = navigationView.getHeaderView(0).findViewById(R.id.main_email);

        /*actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();*/

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfull...";
                        if(!task.isSuccessful()){
                            msg = "Failed";
                        }
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //final MenuItem menuItems = item;
                drawerLayout.closeDrawers();
                if (currentUser != null) {
                    /*drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);

                        }
                    });*/

                    if (previousMenuItem != null) {
                        previousMenuItem.setChecked(false);
                    }
                    item.setChecked(true);
                    item.setCheckable(true);
                    previousMenuItem = item;

                    int menuItem = item.getItemId();
                    if (menuItem == R.id.nav_my_home) {
                        invalidateOptionsMenu();
                        getSupportActionBar().setTitle(R.string.app_name);
                        setFragment(new HomeFragment(), HOME_FRAGMENT);
                    } else if (menuItem == R.id.nav_my_orders) {
                        invalidateOptionsMenu();
                        getSupportActionBar().setTitle("My Orders");
                        //setFragment(new MyOrdersFragment(), ORDERS_FRAGMENT);
                        setFragment(new OrdersFragment(), ORDERS_FRAGMENT);
                    } else if (menuItem == R.id.nav_my_cart) {
                        if (currentUser == null) {
                            signInDialog.show();
                        } else {
                            invalidateOptionsMenu();
                            getSupportActionBar().setTitle("My Cart");
                            setFragment(new MyCartFragment(), CART_FRAGMENT);
                        }
                    } else if (menuItem == R.id.nav_my_wishlist) {
                        invalidateOptionsMenu();
                        getSupportActionBar().setTitle("My Wishlist");
                        setFragment(new MyWishlistFragment(), WISH_FRAGMENT);
                    } else if (menuItem == R.id.nav_my_account) {
                        invalidateOptionsMenu();
                        getSupportActionBar().setTitle("My Account");
                        setFragment(new MyAccountFragment(), MY_ACCOUNT_FRAGMENT);
                    } else if (menuItem == R.id.share) {
                        share();
                    } else if (menuItem == R.id.aboutus) {
                        invalidateOptionsMenu();
                        getSupportActionBar().setTitle("About us");
                        setFragment(new AboutFragment(), ABOUT_US);
                    } else if (menuItem == R.id.nav_sign_out) {
                        FirebaseAuth.getInstance().signOut();
                        GoogleSignIn.getClient(MainActivity.this, new GoogleSignInOptions
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
                                Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        DBqueries.clearData();
                        currentFragment = -1;
                        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                        finish();
                    }

                    //drawerLayout.closeDrawers();
                    return true;
                } else {
                    //drawerLayout.closeDrawers();
                    signInDialog.show();
                    return false;
                }
            }
        });
        navigationView.getMenu().getItem(0).setChecked(true);

        if (showCart) {
            //invalidateOptionsMenu();
            mainActivity = this;
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Cart");
            invalidateOptionsMenu();
            setFragment(new MyCartFragment(), -2);
        } else {
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);

            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            setFragment(new HomeFragment(), HOME_FRAGMENT);
        }

        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigationView.getMenu().getItem(5).setEnabled(false);
        } else {
            FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DBqueries.fullName = task.getResult().getString("full_name");
                                DBqueries.email = task.getResult().getString("user_email");
                                //DBqueries.profile = task.getResult().getString("profile");

                                fullName.setText(DBqueries.fullName);
                                email.setText(DBqueries.email);
                                //Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.ic_person_add_white)).into(profileView)
                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            navigationView.getMenu().getItem(5).setEnabled(true);
        }
        if (resetMainActivity){
            resetMainActivity = false;
            getSupportActionBar().setTitle(R.string.app_name);
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        //invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == HOME_FRAGMENT) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent searchIntent = new Intent(this,SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == R.id.action_share) {
            share();
            return true;
        } else if (id == R.id.action_privacy) {
            privacy();
            return true;
        } else if (id == android.R.id.home) {

            if (showCart) {
                mainActivity = null;
                showCart = false;
                finish();
                return true;
            }
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() == true) {
                Toast.makeText(MainActivity.this, "You also open menu to left slide", Toast.LENGTH_SHORT).show();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return false;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    private void share(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "Best app to buy fruits, vegetables and groceries online and fast delivery to your doorstep.Free your time from the supermarket's long queues, the search parking spaces and carrying it all home.Our user-friendly app is packed with powerful features that will transform your shopping experience.Download this Application to start your unforgettable journey with Saraswati Store:- https://play.google.com/store/apps/details?id=com.appdevelopers.saraswatistore&hl=en_IN&hl=en";
        String sharesub="Saraswati Store";
        intent.putExtra(Intent.EXTRA_SUBJECT,sharesub);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(intent,"ShareVia"));
    }

    private void privacy(){
        Uri uri = Uri.parse("https://piyushguptadevelopers.blogspot.com/2021/05/privacy-policy-piyush-gupta-built.html");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        count = 0;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                if (count == 1) {
                    currentFragment = -1;
                    finishAffinity();
                    //super.onBackPressed();
                }else {
                    Toast.makeText(this,"Press again to exit app!",Toast.LENGTH_SHORT).show();
                }
                count++;
            } else {
                if (showCart) {
                    mainActivity = null;
                    showCart = false;
                    finish();
                } else {
                    invalidateOptionsMenu();
                    getSupportActionBar().setTitle(R.string.app_name);
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }

        }

    }

    /* @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    public void setFragment(Fragment fragment, int fragmentNo) {
        if (fragmentNo != currentFragment) {
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_framelayout, fragment);
            fragmentTransaction.commit();

            if (fragmentNo == CART_FRAGMENT || showCart){
                params.setScrollFlags(0);
            }else {
                params.setScrollFlags(scrollFlags);
            }
        }
    }

    public void setUpToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}