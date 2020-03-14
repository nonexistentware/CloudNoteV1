package com.nonexistentware.cloudnotev1.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nonexistentware.cloudnotev1.Activity.EditNoteActivity;
import com.nonexistentware.cloudnotev1.DB.NoteDataBase;
import com.nonexistentware.cloudnotev1.Interface.ItemClickListener;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private LayoutInflater inflater;
    private List<NoteItem> notes;

    public NoteAdapter(Context context, List<NoteItem> notes) {
        this.inflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item_layout, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder viewHolder, int i) {
        String title = notes.get(i).getNoteTitle().trim();
//        String body = notes.get(i).getNoteBody();
        String date = notes.get(i).getDate();
        String time = notes.get(i).getTime();
        long id = notes.get(i).getId();
        Log.d("date on", "Date on: " + date);

        viewHolder.noteTitle.setText(title);
        viewHolder.noteDate.setText(date);
        viewHolder.noteTime.setText(time);
        viewHolder.noteId.setText(String.valueOf(notes.get(i).getId()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements ItemClickListener {


        TextView noteTitle, noteBody, noteDate, noteTime, noteId;
        private NoteDataBase ndb;

        long id;

        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.cloud_note_title_item);
            noteBody = itemView.findViewById(R.id.cloud_note_body_item);

            noteDate = itemView.findViewById(R.id.note_date_item);
            noteTime = itemView.findViewById(R.id.note_time_item);

            noteId = itemView.findViewById(R.id.listId);

            ndb = new NoteDataBase(itemView.getContext());


//            checkDbRecords();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), EditNoteActivity.class);
                    i.putExtra("ID", notes.get(getAdapterPosition()).getId());
                    v.getContext().startActivity(i);
                }
            });
        }

        @Override
        public void onClick(View view, int position) {

        }
    }
}
