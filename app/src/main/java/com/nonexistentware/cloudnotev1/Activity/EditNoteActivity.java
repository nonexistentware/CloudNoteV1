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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.Calendar;


public class EditNoteActivity extends AppCompatActivity {

    private EditText notetitle, noteBody;
    private FloatingActionButton fab, fabRemove;
    private Calendar calendar;
    private String todayDate;
    private String currentTime;
    long nid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
//        Toolbar toolbar = findViewById(R.id.toolbar_edit_activity);
//        setSupportActionBar(toolbar);

        notetitle = findViewById(R.id.title_note_edit_activity);
        noteBody = findViewById(R.id.body_note_edit_activity);
        fab = findViewById(R.id.fab_editnote_btn_save);
        fabRemove = findViewById(R.id.fab_editnote_btn_delete);

        Intent i = getIntent();
        nid = i.getLongExtra("ID", 0);
        NoteDataBase ndb = new NoteDataBase(this);
        NoteItem note = ndb.getNote(nid);

        final String title = note.getNoteTitle();
        String body = note.getNoteBody();

        notetitle.setText(title);
        noteBody.setText(body);

        calendar = Calendar.getInstance();
        todayDate = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "Date: " + todayDate);
        currentTime = pad(calendar.get(Calendar.HOUR)) + ":" + pad(calendar.get(Calendar.MINUTE));
        Log.d("TIME", "Time: " + currentTime);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteItem note = new NoteItem(nid, notetitle.getText().toString(),
                        noteBody.getText().toString(), todayDate, currentTime);
                NoteDataBase ndb = new NoteDataBase(getApplicationContext());
                long id = ndb.editNote(note);
                goToMain();
            }
        });

        fabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               deleteNote();
            }
        });

        fab.setVisibility(View.INVISIBLE);

        titleChange(); //show save button
        bodyChange(); //show save button

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
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        notetitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fab.setVisibility(View.VISIBLE);
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
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        String note = notetitle.getText().toString();
        String body = noteBody.getText().toString();

        if (!TextUtils.isEmpty(note)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            showAlert(this);
        }
    }
}

