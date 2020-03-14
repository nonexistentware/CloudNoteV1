package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class NewNote extends AppCompatActivity {

    private EditText noteTitle, noteBody;
    private TextView saveBtn, uploadBtn;
    private Calendar calendar;
    private String todayDate;
    private String currentTime;

    private FirebaseAuth auth;
    private DatabaseReference noteReference;
    private String noteId;
    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        auth = FirebaseAuth.getInstance();
        noteReference = FirebaseDatabase.getInstance().getReference().child("CloudNote").child(auth.getCurrentUser().getUid());

        noteTitle = findViewById(R.id.title_note_new_activity);
        noteBody = findViewById(R.id.body_note_new_activity);

        saveBtn = findViewById(R.id.new_note_activity_btn);
        uploadBtn = findViewById(R.id.new_note_upload_btn);
//        removeFab = findViewById(R.id.delete_fab_note_btn);

        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() != 0) {
//                    getSupportActionBar().setTitle(s);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewNote();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noteTitle.getText().toString().trim();
                String body = noteBody.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
                    uploadNote(title, body);
                } else {
                    Snackbar.make(v, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        puData();

        calendar = Calendar.getInstance();
        todayDate = calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "Date: "+todayDate);
        currentTime = pad(calendar.get(Calendar.HOUR))+":"+pad(calendar.get(Calendar.MINUTE));
        Log.d("TIME", "Time: "+currentTime);
    }

    private String pad(int time) {
        if(time < 10)
            return "0"+time;
        return String.valueOf(time);

    }

    public void puData() {
        if (isExist) {
            noteReference.child(noteId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("body")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String body = dataSnapshot.child("body").getValue().toString();

                        noteTitle.setText(title);
                        noteBody.setText(body);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void uploadNote(String title, String body) {
        if (auth.getCurrentUser() != null) {
//            if (isExist) {
//                Map updateMap = new HashMap();
//                updateMap.put("title", noteTitle.getText().toString().trim());
//                updateMap.put("body", noteTitle.getText().toString().trim());
//                updateMap.put("timestamp", ServerValue.TIMESTAMP);
//
//                noteReference.child(noteId).updateChildren(updateMap);

                final DatabaseReference reference = noteReference.push();

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
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                mainThread.start();
            }
        }
//    }


    public void saveNewNote() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading note...");
        progressDialog.show();


        if (noteTitle.getText().length() != 0) {
            NoteItem note = new NoteItem(noteTitle.getText().toString(),
                    noteBody.getText().toString(), todayDate, currentTime);
            NoteDataBase nDB = new NoteDataBase(this);
            long id = nDB.addNote(note);
            NoteItem check = nDB.getNote(id);
            Log.d("inserted", "Note: " + id + " -> Title:" + check.getNoteTitle() + " Date: " + check.getDate());
            onBackPressed();

            progressDialog.dismiss();
            Toast.makeText(this, "Note Saved.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.dismiss();
            noteTitle.setError("Title Can not be Blank.");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //add dialog screen
    }
}
