package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.nonexistentware.cloudnotev1.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailField;
    private TextView backPtn, submitBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = findViewById(R.id.email_restore_field);

        submitBtn = findViewById(R.id.send_email_to_restore);
        backPtn = findViewById(R.id.back_btn_forgot_activity);

        progressBar = findViewById(R.id.forgot_activity_progress);

        auth = FirebaseAuth.getInstance();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();

                if (!TextUtils.isEmpty(email)) {
                    beginRecovery(email);
                } else {
                    Toast.makeText(getApplicationContext(), "Email field can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backPtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
    }

    private void beginRecovery(String email) {
        submitBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    submitBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Password recovery was sent to your email", Toast.LENGTH_SHORT).show();
                } else {
                    submitBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}