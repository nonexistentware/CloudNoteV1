package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.R;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView userMail, removeAccountBtn, removeUserData, signoutBtn;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private String emailId;

    ProgressBar progressBar;

    //dataBase
    private NoteDataBase noteDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userImage = findViewById(R.id.user_profile_image);

        userMail = findViewById(R.id.user_profile_mail);

//        removeAccountBtn = findViewById(R.id.user_remove_account); //remove only account
        removeUserData = findViewById(R.id.user_remove_data); //remove only user data, not account
        signoutBtn = findViewById(R.id.user_sign_out);

        progressBar = findViewById(R.id.user_profile_progress);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("CloudNote")
                .child(auth.getCurrentUser().getUid());

        noteDataBase = new NoteDataBase(this); // initialize database

        progressBar.setVisibility(View.INVISIBLE);

        emailId = getIntent().getStringExtra("userId");

        loadUserData();


        //remove user data
        removeUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserProfileActivity.this, R.style.alertDialog);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("This process will remove notes from cloud");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.VISIBLE);
                        removeUserData.setVisibility(View.INVISIBLE);
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    removeUserData.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(), "Data removed successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    removeUserData.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(), "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    private void loadUserData() {
        userMail.setText(auth.getCurrentUser().getEmail());
        Picasso.with(this)
                .load(currentUser.getPhotoUrl())
                .into(userImage);
    }
}
