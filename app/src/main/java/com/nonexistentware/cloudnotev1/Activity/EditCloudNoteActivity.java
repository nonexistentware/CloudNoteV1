package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.nonexistentware.cloudnotev1.R;

import java.util.HashMap;
import java.util.Map;

public class EditCloudNoteActivity extends AppCompatActivity {

    private TextView removeBtn, saveBtn;
    private EditText cloudNoteTitle, cloudNoteBody;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private String noteId;
    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cloud_note);

        try {
            noteId = getIntent().getStringExtra("noteId");

            if (!noteId.trim().equals("")) {
                isExist = true;
            } else {
                isExist = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        removeBtn = findViewById(R.id.edit_cloud_note_delete_btn);
        saveBtn = findViewById(R.id.edit_cloud_note_save_btn);

        cloudNoteTitle = findViewById(R.id.title_cloud_note_edit_activity);
        cloudNoteBody = findViewById(R.id.body_cloud_note_edit_activity);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("CloudNote")
                .child(auth.getCurrentUser().getUid());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = cloudNoteTitle.getText().toString().trim();
                String body = cloudNoteBody.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
                    editCloudNote(title, body);
                } else {
                    Snackbar.make(view, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCloudNote();
            }
        });

        putData();
    }

    private void putData() {
        if (isExist) {
            databaseReference.child(noteId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("body")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String body = dataSnapshot.child("body").getValue().toString();

                        cloudNoteTitle.setText(title);
                        cloudNoteBody.setText(body);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void editCloudNote(String title, String body) {
        if (auth.getCurrentUser() != null) {
            if (isExist) {
                Map updateNoteMap = new HashMap();
                updateNoteMap.put("title", cloudNoteTitle.getText().toString().trim());
                updateNoteMap.put("body", cloudNoteBody.getText().toString().trim());
                updateNoteMap.put("timestamp", ServerValue.TIMESTAMP);

                databaseReference.child(noteId).updateChildren(updateNoteMap);

                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            } else {

                final DatabaseReference cloudNoteRef = databaseReference.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("body", body);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloudNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Note added to database", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                mainThread.start();
            }
        } else {
            Toast.makeText(this, "USERS IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCloudNote() {
        databaseReference.child(noteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                    noteId = "no";
                    finish();
                } else {
                    Log.e("NewNoteActivity", task.getException().toString());
                    Toast.makeText(getApplicationContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
