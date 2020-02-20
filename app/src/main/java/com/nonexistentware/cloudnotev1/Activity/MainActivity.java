package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nonexistentware.cloudnotev1.Fragment.CloudNoteFragment;
import com.nonexistentware.cloudnotev1.Fragment.DashboardFragment;
import com.nonexistentware.cloudnotev1.R;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth auth;
    FirebaseUser currentUser;

    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 2 ;

    private Uri pickedImgUri = null;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigation;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_main);
        navigationView = findViewById(R.id.nav_view_drawer);

        bottomNavigation = findViewById(R.id.bottom_navigation_bar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main,
                    new DashboardFragment()).commit();
        }

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.dashboard_menu:
                        fragment = new DashboardFragment();
                        break;
                    case R.id.cloud_menu:
                        fragment = new CloudNoteFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main, fragment).commit();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_drawer_register) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }

        if (id == R.id.menu_drawer_login) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        if (id == R.id.menu_drawable_user_profile) {
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        }

        if (id == R.id.menu_drawer_signout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void updateNavHeader() {
        FirebaseUser firebaseUser = this.currentUser;
        if (firebaseUser == null) {
            startActivity(new Intent(getApplication(), RegisterActivity.class));
        } else if (firebaseUser != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView navUserEmail = headerView.findViewById(R.id.drawer_nav_user_mail);
            ImageView navUserImage = headerView.findViewById(R.id.drawer_nav_user_icon);
            //add name view
            navUserEmail.setText(currentUser.getEmail());

            Picasso.with(this)
                    .load(this.currentUser.getPhotoUrl())
                    .into(navUserImage);
        }
    }
}

