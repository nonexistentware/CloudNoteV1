package com.nonexistentware.cloudnotev1.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
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

    public RecyclerView recyclerView2;
    public RecyclerView.LayoutManager layoutManager;

    MaterialSearchBar materialSearchBar;
    List<String> suggestList = new ArrayList<>();
    NoteDataBase dataBase;

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

        //search section
        recyclerView2 = itemView.findViewById(R.id.recycler_search);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        materialSearchBar = itemView.findViewById(R.id.search_edit_text_fragment);
        dataBase = new NoteDataBase(getContext());
        materialSearchBar.setHint("Enter note title");
        loadSuggestList();
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()));
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    adapter = new NoteAdapter(getActivity().getBaseContext(), dataBase.getAllNotes());
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

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
        return itemView;
    }

    //search section
    private void startSearch(String text) {
        adapter = new NoteAdapter(getContext(), dataBase.getNoteByTitle(text));
        recyclerView.setAdapter(adapter);
    }

    public void loadSuggestList() {
        suggestList = dataBase.getNotes();
        materialSearchBar.setLastSuggestions(suggestList);
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
