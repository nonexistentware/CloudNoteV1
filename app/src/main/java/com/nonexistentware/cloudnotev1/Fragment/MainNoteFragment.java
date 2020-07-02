package com.nonexistentware.cloudnotev1.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.nonexistentware.cloudnotev1.Activity.NewNote;
import com.nonexistentware.cloudnotev1.Adapter.NoteAdapter;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.ArrayList;
import java.util.List;


public class MainNoteFragment extends Fragment{

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private TextView noItemText;
    private NoteDataBase noteDataBase;
    private FloatingActionButton fab;

    public RecyclerView.LayoutManager layoutManager;

    NoteDataBase dataBase;

    private FirebaseAuth auth;

    private List<NoteItem> noteList = new ArrayList<>();
    long id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard, parent, false);
        recyclerView = itemView.findViewById(R.id.dashboard_fragment_recycler);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        auth = FirebaseAuth.getInstance();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        dataBase = new NoteDataBase(getContext());

        adapter = new NoteAdapter(getContext(), dataBase.getAllNotes());
        recyclerView.setAdapter(adapter);

        fab = itemView.findViewById(R.id.fab_new_note_activity);

        Intent i = getActivity().getIntent();
        id = i.getLongExtra("ID", 0);

        adapter = new NoteAdapter(getContext(), noteList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

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

        loadUserData();
        return itemView;
    }

    private void displayList(List<NoteItem> allNotes) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NoteAdapter(getContext() , allNotes);
        recyclerView.setAdapter(adapter);
    }

    private void loadUserData() {
        if (auth.getCurrentUser() != null) {

        } else {
            if (auth.getCurrentUser() == null) {
            }
        }
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
