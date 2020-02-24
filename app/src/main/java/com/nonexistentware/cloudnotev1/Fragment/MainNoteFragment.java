package com.nonexistentware.cloudnotev1.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nonexistentware.cloudnotev1.Adapter.NoteAdapter;
import com.nonexistentware.cloudnotev1.CallBack.MainActionModeCallback;
import com.nonexistentware.cloudnotev1.DB.NoteItemDB;
import com.nonexistentware.cloudnotev1.DB.NoteItemDao;
import com.nonexistentware.cloudnotev1.Activity.EditNoteActivity;
import com.nonexistentware.cloudnotev1.Interface.NoteEventListener;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;
import com.nonexistentware.cloudnotev1.Utils.NoteUtils;

import java.util.ArrayList;
import java.util.List;

import static com.nonexistentware.cloudnotev1.Activity.EditNoteActivity.NOTE_EXTRA_Key;


public class MainNoteFragment extends Fragment implements NoteEventListener, ActionMode.Callback {

    private static final String TAG = "MainNoteFragment";
    private RecyclerView recyclerView;
    private ArrayList<NoteItem> notes;
    private NoteAdapter adapter;
    private NoteItemDao dao;
    private MainActionModeCallback actionModeCallback;
    private int chackedCount = 0;
    private FloatingActionButton fab;
    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";
    private int theme;

    private TextView emptyListTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard, parent, false);

        recyclerView = itemView.findViewById(R.id.recycler_notes_list_content_main);
        emptyListTxt = itemView.findViewById(R.id.empty_notes_view_content_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab = itemView.findViewById(R.id.new_fab_note_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewNote();
            }
        });

        dao = NoteItemDB.getInstance(getContext()).noteItemDao();
        return itemView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<NoteItem> list = dao.getNotes();
        this.notes.addAll(list);
        this.adapter = new NoteAdapter(getContext(), this.notes);
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();
        // add swipe helper to recyclerView

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);
    }

    private void showEmptyView() {
        if (notes.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyListTxt.findViewById(R.id.empty_notes_view_content_main).setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListTxt.findViewById(R.id.empty_notes_view_content_main).setVisibility(View.VISIBLE);

        }
    }

    private void onAddNewNote() {
        startActivity(new Intent(getContext(), EditNoteActivity.class));
    }


    @Override
    public void onNoteClick(NoteItem note) {
        Intent edit = new Intent(getContext(), EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);
    }

    @Override
    public void onNoteLongClick(final NoteItem note) {
        note.setChecked(true);
        chackedCount = 1;
        adapter.setMultiCheckMode(true);

        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(NoteItem noteItem) {
                note.setChecked(!note.isChecked());
                if (note.isChecked())
                    chackedCount++;
                else chackedCount--;

                if (chackedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else {
                    actionModeCallback.changeShareItemVisible(true);
                }

                if (chackedCount == 0) {
                    actionModeCallback.getAction().finish();
                }

                actionModeCallback.setCountItem(chackedCount + "/" + notes.size());
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onNoteLongClick(NoteItem noteItem) {

            }
        });

        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_notes)
                    deleteMultiItem();
                else if (menuItem.getItemId() == R.id.action_share_note)
                    shareNotes();

                actionMode.finish();
                return false;
            }
        };


    }

    private void shareNotes() {
        NoteItem note = adapter.getCheckedNotes().get(0);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plane");
        String notetext = note.getNoteTitle() + "\n\n Create on : " +
                NoteUtils.dateFormLong(note.getNoteDate()) + "\n  By :" +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);
    }

    private void deleteMultiItem() {
        List<NoteItem> chackedNotes = adapter.getCheckedNotes();
        if (chackedNotes.size() != 0) {
            for (NoteItem note : chackedNotes) {
                dao.deleteNote(note);
            }
            // refresh Notes
            loadNotes();
            Toast.makeText(getContext(), chackedNotes.size() + " Note(s) Delete successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getContext(), "No Note(s) selected", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.setMultiCheckMode(false);
        adapter.setListener(this);
        fab.setVisibility(View.VISIBLE);
    }

    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // TODO: 28/09/2018 delete note when swipe

                    if (notes != null) {
                        // get swiped note
                        NoteItem swipedNote = notes.get(viewHolder.getAdapterPosition());
                        if (swipedNote != null) {
                            swipeToDelete(swipedNote, viewHolder);

                        }

                    }
                }
            });

    private void swipeToDelete(final NoteItem swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Delete Note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018 delete note
                        dao.deleteNote(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//                        showEmptyView();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018  Undo swipe and restore swipedNote
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());


                    }
                })
                .setCancelable(false)
                .create().show();

    }


}
