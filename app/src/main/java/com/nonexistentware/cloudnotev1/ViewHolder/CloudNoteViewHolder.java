package com.nonexistentware.cloudnotev1.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nonexistentware.cloudnotev1.Interface.ItemClickListener;
import com.nonexistentware.cloudnotev1.R;

public class CloudNoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView noteTime, noteTitle;
    public ImageView noteImage;

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

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
    }
}
