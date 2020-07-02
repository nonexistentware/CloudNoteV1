package com.nonexistentware.cloudnotev1.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewNote extends AppCompatActivity {

    private EditText noteTitle, noteBody;
    private TextView saveBtn;
    private Calendar calendar;
    private String todayDate;
    private String currentTime;
    //mic
    private ImageView titleMic, bodyMic;
    private static final int REQUEST_CODE_SPEECH_INPUT_TITLE = 1000;
    private static final int REQUEST_CODE_SPEECH_INPUT_BODY = 1001;
    static int PReqCode = 1;
    static int REQUESCODE = 1;

    private String noteId;
    private boolean isExist;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        noteTitle = findViewById(R.id.title_note_new_activity);
        noteBody = findViewById(R.id.body_note_new_activity);

        saveBtn = findViewById(R.id.new_note_activity_btn);
        //mic
        titleMic = findViewById(R.id.new_note_title_mic_btn);
        bodyMic = findViewById(R.id.new_note_body_mic_btn);

        titleMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechTitleRecogniser();
            }
        });

        bodyMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechBodyRecognizer();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewNote();
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

    private void speechTitleRecogniser() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_TITLE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void speechBodyRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_BODY);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_CODE_SPEECH_INPUT_TITLE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> resultTitle = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    noteTitle.setText(resultTitle.get(0));

                }
                break;
            }

            case REQUEST_CODE_SPEECH_INPUT_BODY: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> resultBody = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    noteBody.setText(resultBody.get(0));

                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
