package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nonexistentware.cloudnotev1.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference fUserDatabase;

    private ImageView userImg;
    private TextView createUserBtn, toLogin, registerIndTxt;
    private EditText userMail, userPass;

    ProgressBar progressBar;

    Uri pickedImageUri;

    static int PReqCode = 1;
    static int REQUESCODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userImg = findViewById(R.id.user_image_reg);

        createUserBtn = findViewById(R.id.user_reg_btn);

        userMail = findViewById(R.id.user_email_reg);
        userPass = findViewById(R.id.user_pass_reg);

        toLogin = findViewById(R.id.to_login_screen);

        progressBar = findViewById(R.id.register_progress);
        registerIndTxt = findViewById(R.id.create_text_indicator);

        auth = FirebaseAuth.getInstance();
        fUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 29) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = userMail.getText().toString().trim();
                String pass = userPass.getText().toString().trim();

                if (!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass)) {
                    createNewUser(mail, pass);
                } else {
                    Toast.makeText(getApplicationContext(), "Email and password fields can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
        registerIndTxt.setVisibility(View.INVISIBLE);

    }

    private void createNewUser(final String mail, String pass) {
        createUserBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        registerIndTxt.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                registerIndTxt.setText("This is taking longer than expected");
            }
        }, 5000);
        auth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && pickedImageUri == null) {
                            progressBar.setVisibility(View.INVISIBLE);
                            registerIndTxt.setVisibility(View.INVISIBLE);
                            updateUserInfo(mail, auth.getCurrentUser());
                            fUserDatabase.child(auth.getCurrentUser().getUid()).child("email").setValue(mail);
                            Toast.makeText(RegisterActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful() && pickedImageUri != null){
                            progressBar.setVisibility(View.INVISIBLE);
                            registerIndTxt.setVisibility(View.INVISIBLE);
                            fUserDatabase.child(auth.getCurrentUser().getUid()).child("email").setValue(mail);
                            createProfileWithImage(mail, pickedImageUri, auth.getCurrentUser());
                            Toast.makeText(RegisterActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            createUserBtn.setVisibility(View.VISIBLE);
                            registerIndTxt.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //no profile img
    private void updateUserInfo(final String email, final FirebaseUser currentUser) {
        final UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(email)
                .build();
        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateUi();
                        }
                    }
                });
    }

    //with profile img
    private void createProfileWithImage(final String email, final Uri pickedImageUri, final FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(email)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // user info updated successfully
//                                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
//                                            showMessage("Register Complete");
                                            updateUi();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }


    private void updateUi() {
        progressBar.setVisibility(View.VISIBLE);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImageUri = data.getData();
            userImg.setImageURI(pickedImageUri);
        }
    }
 }