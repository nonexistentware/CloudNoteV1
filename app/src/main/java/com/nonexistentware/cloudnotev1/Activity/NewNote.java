package com.nonexistentware.cloudnotev1.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.Calendar;

public class NewNote extends AppCompatActivity {

    private EditText noteTitle, noteBody;
    private FloatingActionButton fab, removeFab;
    private Calendar calendar;
    private String todayDate;
    private String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        noteTitle = findViewById(R.id.title_note_new_activity);
        noteBody = findViewById(R.id.body_note_new_activity);

        fab = findViewById(R.id.new_fab_note_btn);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewNote();
            }
        });

        removeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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

    public void saveNewNote() {
        if (noteTitle.getText().length() != 0) {
            NoteItem note = new NoteItem(noteTitle.getText().toString(),
                    noteBody.getText().toString(), todayDate, currentTime);
            NoteDataBase nDB = new NoteDataBase(this);
            long id = nDB.addNote(note);
            NoteItem check = nDB.getNote(id);
            Log.d("inserted", "Note: " + id + " -> Title:" + check.getNoteTitle() + " Date: " + check.getDate());
            onBackPressed();

            Toast.makeText(this, "Note Saved.", Toast.LENGTH_SHORT).show();
        } else {
            noteTitle.setError("Title Can not be Blank.");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //add dialog screen
    }
}
