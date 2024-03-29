package com.nonexistentware.cloudnotev1.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nonexistentware.cloudnotev1.Interface.ItemClickListener;
import com.nonexistentware.cloudnotev1.R;

public class CloudNoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View view;
    public FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    public FirebaseUser currentUser;
    public String noteId;

    ItemClickListener itemClickListener;
    
    TextView cloudNoteTitle, cloudNoteTime;
    public TextView noteTime, noteTitle;
    public ImageView noteImage;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public CloudNoteViewHolder(@NonNull final View itemView) {
        super(itemView);

        cloudNoteTitle = itemView.findViewById(R.id.cloud_note_title_view);
        cloudNoteTime = itemView.findViewById(R.id.cloud_note_time);

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
    }
}