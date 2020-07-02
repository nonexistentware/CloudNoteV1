package com.nonexistentware.cloudnotev1.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nonexistentware.cloudnotev1.Activity.ForgotPasswordActivity;
import com.nonexistentware.cloudnotev1.Activity.LoginActivity;
import com.nonexistentware.cloudnotev1.Activity.RegisterActivity;
import com.nonexistentware.cloudnotev1.R;
import com.squareup.picasso.Picasso;


public class UserFragment extends Fragment {

    private ImageView userImage;
    private TextView userMail, signoutBtn, loginBtn, registerBtn, forgotBtn;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_user, container, false);

        userImage = itemView.findViewById(R.id.user_profile_image);
        userMail = itemView.findViewById(R.id.user_profile_mail);

        signoutBtn = itemView.findViewById(R.id.user_sign_out);

        loginBtn = itemView.findViewById(R.id.login_btn);
        registerBtn = itemView.findViewById(R.id.register_btn);
        forgotBtn = itemView.findViewById(R.id.forgot_btn);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RegisterActivity.class));
            }
        });

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
            }
        });


        if (auth.getCurrentUser() != null) {
            reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(auth.getCurrentUser().getUid());
        }

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userMail.setText("Offline");
                        signoutBtn.setVisibility(View.INVISIBLE);
                        loginBtn.setVisibility(View.VISIBLE);
                        registerBtn.setVisibility(View.VISIBLE);
                        Picasso.with(getContext())
                                .load(R.drawable.user_account_white)
                                .into(userImage);
                    }
                });

            }
        });

        loadUserData();

        return itemView;
    }

    private void loadUserData() {
        if (auth.getCurrentUser() != null) {
            userMail.setText(currentUser.getEmail());
            loginBtn.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.INVISIBLE);
            Picasso.with(getContext())
                    .load(currentUser.getPhotoUrl())
                    .into(userImage);
        } else {
            if (auth.getCurrentUser() == null) {
                userMail.setText("Offline");
                signoutBtn.setVisibility(View.INVISIBLE);
                Picasso.with(getContext())
                        .load(R.drawable.user_account_white)
                        .into(userImage);
            }
        }
    }
}
