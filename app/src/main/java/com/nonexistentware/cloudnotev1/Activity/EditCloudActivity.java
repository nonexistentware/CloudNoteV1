package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class EditCloudActivity extends AppCompatActivity {

    private EditText cloudNoteTitle, cloudNoteBody;
    private TextView saveBtn, removeBtn;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    private String noteId = "no";



    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cloud);

        try {
            noteId = getIntent().getStringExtra("noteId");
            if (!noteId.trim().equals("no")) {
                isExist = true;
            } else {
                isExist = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReference(Common.STR_CLOUD_NOTE);
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("CloudNote").child(auth.getCurrentUser().getUid());





        cloudNoteTitle = findViewById(R.id.cloud_edit_note_title_activity);
        cloudNoteBody = findViewById(R.id.cloud_edit_note_body_activity);

        removeBtn = findViewById(R.id.cloud_note_remove_btn);
        saveBtn = findViewById(R.id.cloud_note_update_btn);

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNote();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = cloudNoteTitle.getText().toString().trim();
                String body = cloudNoteBody.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
                    updateNote(title, body);
                } else {

                }
            }
        });

        pudData();

    }

    private void pudData() {
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

    private void updateNote(String title, String body) {
        if (auth.getCurrentUser() != null) {
            if (isExist) {
                Map updateMap = new HashMap();
                updateMap.put("title", cloudNoteTitle.getText().toString().trim());
                updateMap.put("body", cloudNoteBody.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);

                databaseReference.child(noteId).updateChildren(updateMap);
                Toast.makeText(this, "Note updated0", Toast.LENGTH_SHORT).show();

            } else {
                final DatabaseReference reference = databaseReference.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("body", body);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        reference.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditCloudActivity.this, "Note added to database", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditCloudActivity.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void removeNote() {
        databaseReference.child(noteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
