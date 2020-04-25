package com.nonexistentware.cloudnotev1.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nonexistentware.cloudnotev1.Adapter.NoteAdapter;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.List;

public class ShowNotesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private TextView noItemText;
    private NoteDataBase noteDataBase;
    private FloatingActionButton fab;


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);

        noteDataBase = new NoteDataBase(this);
        List<NoteItem> allNotes = noteDataBase.getAllNotes();
        recyclerView = findViewById(R.id.recycler_show_notes);

        displayList(allNotes);


    }

    private void displayList(List<NoteItem> allNotes) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(this,allNotes);
        recyclerView.setAdapter(adapter);
    }

}
