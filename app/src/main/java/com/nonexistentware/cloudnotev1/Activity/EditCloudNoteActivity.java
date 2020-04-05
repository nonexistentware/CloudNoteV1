package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditCloudNoteActivity extends AppCompatActivity {

    private TextView removeBtn, saveBtn, saveSqlBtn;
    private EditText cloudNoteTitle, cloudNoteBody;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private Calendar calendar;
    private String todayDate;
    private String currentTime;

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
        saveSqlBtn = findViewById(R.id.edit_cloud_note_save_to_sql_btn);

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditCloudNoteActivity.this, R.style.alertDialog);
                dialog.setTitle("Remove cloud note");
                dialog.setMessage("Do you want to remove this note from cloud?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCloudNote();
                        onBackPressed();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        saveSqlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSqlMethod();
            }
        });

        calendar = Calendar.getInstance();
        todayDate = calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "Date: "+todayDate);
        currentTime = pad(calendar.get(Calendar.HOUR))+":"+pad(calendar.get(Calendar.MINUTE));
        Log.d("TIME", "Time: "+currentTime);

        putData();
    }

    private String pad(int time) {
        if(time < 10)
            return "0"+time;
        return String.valueOf(time);

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

    private void saveToSqlMethod() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading note...");
        progressDialog.show();


        if (cloudNoteTitle.getText().length() != 0) {
            NoteItem note = new NoteItem(cloudNoteTitle.getText().toString(),
                    cloudNoteTitle.getText().toString(), todayDate, currentTime);
            NoteDataBase nDB = new NoteDataBase(this);
            long id = nDB.addNote(note);
            NoteItem check = nDB.getNote(id);
            Log.d("inserted", "Note: " + id + " -> Title:" + check.getNoteTitle() + " Date: " + check.getDate());
//            onBackPressed();

            progressDialog.dismiss();
            Toast.makeText(this, "Note Saved.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.dismiss();
            cloudNoteTitle.setError("Title Can not be Blank.");
        }
    }
}
