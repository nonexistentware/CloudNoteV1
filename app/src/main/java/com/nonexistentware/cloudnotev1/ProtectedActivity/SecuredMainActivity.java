package com.nonexistentware.cloudnotev1.ProtectedActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nonexistentware.cloudnotev1.Activity.MainActivity;
import com.nonexistentware.cloudnotev1.Fragment.SecuredCloudFragment;
import com.nonexistentware.cloudnotev1.Fragment.SecuredDashboardFragment;
import com.nonexistentware.cloudnotev1.Fragment.UserFragment;
import com.nonexistentware.cloudnotev1.R;

public class SecuredMainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser currentUser;

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secured_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        bottomNavigation = findViewById(R.id.bottom_navigation_secured_bar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_secured_main,
                    new SecuredDashboardFragment()).commit();
        }

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.secured_dashboard_menu:
                        fragment = new SecuredDashboardFragment();
                        break;
                    case R.id.secured_cloud_menu:
                        fragment = new SecuredCloudFragment();
                        break;
                    case R.id.secured_account_menu:
                        fragment = new UserFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_secured_main, fragment).commit();
                return true;
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SecuredMainActivity.this, MainActivity.class));
            super.onKeyDown(keyCode, event);
            return true;
        }
        return false;
    }
}