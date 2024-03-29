package com.nonexistentware.cloudnotev1.ProtectedActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
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
import com.nonexistentware.cloudnotev1.Activity.LoginActivity;
import com.nonexistentware.cloudnotev1.Activity.SecondLoginActivity;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.DB.SecuredDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditSecuredNoteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    private EditText noteTitle, noteBody;
    private TextView saveBtn, deleteBtn;
    private Calendar calendar;
    private String todayDate;
    private String currentTime;
    long nid;

    public View v;

    private FirebaseAuth auth;
    private DatabaseReference noteReference;
    private String noteId;
    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_secured_note_activity);

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

        noteTitle = findViewById(R.id.title_secured_note_edit_activity);
        noteBody = findViewById(R.id.body_secured_note_edit_activity);
        saveBtn = findViewById(R.id.editnote_secured_btn_save);
        deleteBtn = findViewById(R.id.editnote_secured_btn_delete);

        Intent i = getIntent();
        nid = i.getLongExtra("ID", 0);
        SecuredDataBase sdb = new SecuredDataBase(this);
        NoteItem note = sdb.getNote(nid);

        final String title = note.getNoteTitle();
        String body = note.getNoteBody();


        auth = FirebaseAuth.getInstance();


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
                SecuredDataBase ndb = new SecuredDataBase(getApplicationContext());
                long id = ndb.editNote(note);

                Toast.makeText(getApplicationContext(), "Note updated", Toast.LENGTH_SHORT).show();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditSecuredNoteActivity.this, R.style.alertDialog);
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

        putData();

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
                            }  if (auth.getCurrentUser() == null) {
                                startActivity(new Intent(getApplicationContext(), SecondLoginActivity.class));
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

    private void deleteNote() {
        SecuredDataBase db = new SecuredDataBase(getApplicationContext());
        db.deleteNote(nid);
        startActivity(new Intent(getApplicationContext(), SecuredMainActivity.class));
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

    private void showCloudNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NotificationChannel mChannel = new NotificationChannel("1", "cloudChannel",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        SharedPreferences prefs = getSharedPreferences(EditSecuredNoteActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int notificationNumber = prefs.getInt("notificationNumber", 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.ic_add_alert_white_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        notificationManager.notify(notificationNumber, mBuilder.build());
        SharedPreferences.Editor editor = prefs.edit();
        notificationNumber++;
        editor.putInt("notificationNumber", notificationNumber);
        editor.commit();
    }

    public void showPopUp(View v) {
        Context wrapper = new ContextThemeWrapper(this, R.style.alertDialog);
        PopupMenu popupMenu = new PopupMenu(wrapper, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_secured_bottom_bar);
        popupMenu.show();
    }

    private void emailWithSubject() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        String[] recipients={};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, noteTitle.getText().toString());
        intent.putExtra(Intent.EXTRA_TEXT, noteBody.getText().toString());
        intent.putExtra(Intent.EXTRA_CC,"");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        try {
            startActivity(Intent.createChooser(intent, "Send mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EditSecuredNoteActivity.this, "Could not detect Gmail on your device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void emailNoSubject() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        String[] recipients={};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, noteBody.getText().toString());
        intent.putExtra(Intent.EXTRA_CC,"");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        try {
            startActivity(Intent.createChooser(intent, "Send mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EditSecuredNoteActivity.this, "Could not detect Gmail on your device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createLetter() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.alertDialog);
        dialog.setTitle("Do you want to create letter?");
        dialog.setMessage("Create letter using note title as letter subject?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailWithSubject();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailNoSubject();
            }
        });
        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void protectedUpload(View v) {
        if (auth.getCurrentUser() != null) {
            noteReference = FirebaseDatabase.getInstance().getReference().child("SecuredCloudNote").child(auth.getCurrentUser().getUid());

            String title = noteTitle.getText().toString().trim();
            String body = noteBody.getText().toString().trim();

            if (!TextUtils.isEmpty(title)) {
                uploadNote(title, body);
                Toast.makeText(getApplicationContext(), "Successfully uploaded to protected cloud.", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(v, "Note title can't be empty.", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            startActivity(new Intent(this, SecondLoginActivity.class));
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_secured_note_upload_secured_cloud:
                protectedUpload(v);
                return true;
            case R.id.menu_secured_note_add_calendar:
                exportToCalendar();
                return true;
            case R.id.menu_secured_note_notification:
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();
                showCloudNotification(this, title, body);
                return true;
            case R.id.menu_secured_note_email:
                createLetter();
                return true;
    }
    return false;
}
}