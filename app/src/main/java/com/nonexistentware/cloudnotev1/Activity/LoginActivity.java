package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.nonexistentware.cloudnotev1.R;

public class LoginActivity extends AppCompatActivity {

    private TextView forgotPassBtn, loginBtn, toRegister;
    private EditText userInputMail, userInputPass;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        userInputMail = findViewById(R.id.user_email_login);
        userInputPass = findViewById(R.id.user_pass_login);

        toRegister = findViewById(R.id.move_to_register_screen);
        loginBtn = findViewById(R.id.user_login_btn);
        forgotPassBtn = findViewById(R.id.user_forgot_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = userInputMail.getText().toString().trim();
                String pass = userInputPass.getText().toString().trim();

                if (!TextUtils.isEmpty(mail) || !TextUtils.isEmpty(pass)) {
                   logIn(mail, pass);
                }

            }
        });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

    }

    private void logIn(String mail, String pass) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in, please wait...");
        progressDialog.show();

        auth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                           if (task.isSuccessful()) {
                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                               updateUI();
                               finish();
                               Toast.makeText(getApplicationContext(), "Sing in successfully", Toast.LENGTH_SHORT).show();
                           } else {
                               Toast.makeText(getApplicationContext(), "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                           }
                    }
                });
    }

    private void updateUI() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            updateUI();
        }
    }
}
