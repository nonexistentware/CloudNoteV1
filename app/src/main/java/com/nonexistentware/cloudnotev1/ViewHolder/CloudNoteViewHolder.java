package com.nonexistentware.cloudnotev1.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
<<<<<<< HEAD
import android.widget.Toast;
=======
>>>>>>> 9e6995f04015233c601c2e7f4a7c771d3320f7f9

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
=======
>>>>>>> 9e6995f04015233c601c2e7f4a7c771d3320f7f9
import com.nonexistentware.cloudnotev1.Interface.ItemClickListener;
import com.nonexistentware.cloudnotev1.R;

public class CloudNoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

<<<<<<< HEAD
    View view;

    public FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    public FirebaseUser currentUser;
    public String noteId;

    ItemClickListener itemClickListener;


    TextView cloudNoteTitle, cloudNoteTime;
    public ImageView removeBtn;

=======
    public TextView noteTime, noteTitle;
    public ImageView noteImage;

    ItemClickListener itemClickListener;

>>>>>>> 9e6995f04015233c601c2e7f4a7c771d3320f7f9
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

<<<<<<< HEAD
    public CloudNoteViewHolder(@NonNull final View itemView) {
        super(itemView);

        cloudNoteTitle = itemView.findViewById(R.id.cloud_note_title_view);
        cloudNoteTime = itemView.findViewById(R.id.cloud_note_time);
        removeBtn = itemView.findViewById(R.id.cloud_note_remove_btn);

        removeBtn.setVisibility(View.INVISIBLE);

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(itemView.getContext(), "Note removed from cloud successfully", Toast.LENGTH_SHORT).show();
//            }
//        });

        itemView.setOnClickListener(this);

    }

    public void setCloudNoteTitle(String title) {
        cloudNoteTitle.setText(title);
    }

    public void setCloudNoteTime(String time) {
        cloudNoteTime.setText(time);
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
=======
    public CloudNoteViewHolder(@NonNull View itemView) {
        super(itemView);

        this.noteTitle = itemView.findViewById(R.id.cloud_note_title_item);
        this.noteTime = itemView.findViewById(R.id.cloud_note_time_item);

        itemView.setOnClickListener(this);
    }

    public void setNoteTime(String time) {
        noteTime.setText(time);
    }

    public void setNoteTitle(String title) {
        noteTitle.setText(title);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
>>>>>>> 9e6995f04015233c601c2e7f4a7c771d3320f7f9
    }
}
