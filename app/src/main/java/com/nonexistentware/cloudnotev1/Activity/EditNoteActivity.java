package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class EditNoteActivity extends AppCompatActivity {

    private EditText noteTitle, noteBody;
    private TextView saveBtn, uploadBtn, deleteBtn, calendarBtn;
    private Calendar calendar;
    private String todayDate;
    private String currentTime;
    long nid;

    private FirebaseAuth auth;
    private DatabaseReference noteReference;
    private String noteId;
    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

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

        noteTitle = findViewById(R.id.title_note_edit_activity);
        noteBody = findViewById(R.id.body_note_edit_activity);
        saveBtn = findViewById(R.id.editnote_btn_save);
        uploadBtn = findViewById(R.id.editnote_btn_upload);
        deleteBtn = findViewById(R.id.editnote_btn_delete);
        calendarBtn = findViewById(R.id.editnote_btn_export_calendar);

        Intent i = getIntent();
        nid = i.getLongExtra("ID", 0);
        NoteDataBase ndb = new NoteDataBase(this);
        NoteItem note = ndb.getNote(nid);

        final String title = note.getNoteTitle();
        String body = note.getNoteBody();


        auth = FirebaseAuth.getInstance();
        noteReference = FirebaseDatabase.getInstance().getReference().child("CloudNote").child(auth.getCurrentUser().getUid());

        noteTitle.setText(title);
        noteBody.setText(body);

        calendar = Calendar.getInstance();
        todayDate = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "Date: " + todayDate);
        currentTime = pad(calendar.get(Calendar.HOUR)) + ":" + pad(calendar.get(Calendar.MINUTE));
        Log.d("TIME", "Time: " + currentTime);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteItem note = new NoteItem(nid, noteTitle.getText().toString(),
                        noteBody.getText().toString(), todayDate, currentTime);
                NoteDataBase ndb = new NoteDataBase(getApplicationContext());
                long id = ndb.editNote(note);

                Toast.makeText(getApplicationContext(), "Note updated", Toast.LENGTH_SHORT).show();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AlertDialog.Builder dialog = new AlertDialog.Builder(EditNoteActivity.this, R.style.alertDialog);
                dialog.setTitle("Remove note");
                dialog.setMessage("Do you want to remove this note from your device?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                        onBackPressed();
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

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToCalendar();
            }
        });

        putData();

//        saveBtn.setVisibility(View.GONE);
//
//        titleChange(); //show save button
//        bodyChange(); //show save button

    }

    public void putData() {
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
                                    Toast.makeText(getApplicationContext(), "Successfully uploaded to cloud.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed while uploading to cloud." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                mainThread.start();
            }
        }


    private String pad(int time) {
        if (time < 10)
            return "0" + time;
        return String.valueOf(time);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void goToMain() {
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

    public void showAlert(Context aContext) {
        new AlertDialog.Builder(aContext)
                .setTitle("Delete")
                .setMessage("")
                .setMessage("Discard")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .show();


    }

    private void deleteNote() {
        NoteDataBase db = new NoteDataBase(getApplicationContext());
        db.deleteNote(nid);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void titleChange() { //show save button
        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void bodyChange()  { //show save button
        noteBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        String note = noteTitle.getText().toString();
        String body = noteBody.getText().toString();

        if (!TextUtils.isEmpty(note)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            showAlert(this);
        }
    }

    private void exportToCalendar() {
        NoteItem noteItem = new NoteItem();
        Calendar calendarEvent = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", noteTitle.getText().toString());
        intent.putExtra("description", noteBody.getText().toString());
        startActivity(intent);
    }
}

