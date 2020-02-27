package com.nonexistentware.cloudnotev1.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nonexistentware.cloudnotev1.Activity.NewNote;
import com.nonexistentware.cloudnotev1.Adapter.NoteAdapter;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.List;


public class MainNoteFragment extends Fragment {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private TextView noItemText;
    private NoteDataBase noteDataBase;
    private FloatingActionButton fab;

    private Button removeBtn, uploadBtn;
    long id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard, parent, false);


        fab = itemView.findViewById(R.id.fab_new_note_activity);

        removeBtn = itemView.findViewById(R.id.delete_note_item);
        uploadBtn = itemView.findViewById(R.id.upload_btn_item);

        Intent i = getActivity().getIntent();
        id = i.getLongExtra("ID", 0);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NewNote.class));
            }
        });

        noItemText = itemView.findViewById(R.id.noItemText);
        noteDataBase = new NoteDataBase(getContext());
        List<NoteItem> allNotes = noteDataBase.getAllNotes();
        recyclerView = itemView.findViewById(R.id.dashboard_fragment_recycler);

        if (allNotes.isEmpty()) {
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.INVISIBLE);
            displayList(allNotes);

        }


        return itemView;
    }

    private void displayList(List<NoteItem> allNotes) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NoteAdapter(getContext() , allNotes);
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        List<NoteItem> getAllNotes = noteDataBase.getAllNotes();
        if (getAllNotes.isEmpty()) {
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.GONE);
            displayList(getAllNotes);
        }
    }
}
