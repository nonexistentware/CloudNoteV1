package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nonexistentware.cloudnotev1.Common.Common;
import com.nonexistentware.cloudnotev1.R;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView userMail, removeAccountBtn, removeUserData, removeAccountData;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userImage = findViewById(R.id.user_profile_image);

        userMail = findViewById(R.id.user_profile_mail);

        removeAccountBtn = findViewById(R.id.user_remove_account); //remove only account
        removeUserData = findViewById(R.id.user_remove_data); //remove only user data, not account
        removeAccountData = findViewById(R.id.user_remove_accountdata); //remove account and user data



        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Common.STR_USERS);

        loadUserData();

        //remove account
        removeAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserProfileActivity.this);
                dialog.setTitle("Do you really want to remove your account");
                dialog.setMessage("Your data won't be wiped from cloud");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Account successfully removed", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
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

        //remove user data
        removeUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserProfileActivity.this);
                dialog.setTitle("Do you really want to remove your data");
                dialog.setMessage("This process will remove notes from cloud");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("email").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Data removed successfully", Toast.LENGTH_SHORT).show();
                                } else {
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

//    private void removeUserAccount() {
//        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                Toast.makeText(getApplicationContext(), "Account removed successfully", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
//                }
//            }
//        });
    }

//    public void removeUserData() {
//        firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("email").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), "Data removed successfully", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void loadUserData() {
        userMail.setText(auth.getCurrentUser().getEmail());
        Picasso.with(this)
                .load(currentUser.getPhotoUrl())
                .into(userImage);
    }
}
