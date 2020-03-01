package com.nonexistentware.cloudnotev1.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nonexistentware.cloudnotev1.Activity.MainActivity;
import com.nonexistentware.cloudnotev1.Interface.ItemClickListener;
import com.nonexistentware.cloudnotev1.TimeUtil.GetTimeAgo;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;
import com.nonexistentware.cloudnotev1.ViewHolder.CloudNoteViewHolder;


public class CloudNoteFragment extends Fragment {

    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;

    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cloud_note, parent, false);

        recyclerView = itemView.findViewById(R.id.cloud_note_recycler);
        gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            reference = FirebaseDatabase.getInstance().getReference().child("CloudNote").child(auth.getCurrentUser().getUid());
        }

        updateUI();
        loadDate();


        return itemView;
    }

    private void loadDate() {
        Query query = reference.orderByValue();
        FirebaseRecyclerAdapter<NoteItem, CloudNoteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoteItem, CloudNoteViewHolder>(
          NoteItem.class,
          R.layout.cloud_layout_note_item,
          CloudNoteViewHolder.class,
          query
        ) {
            @Override
            protected void populateViewHolder(final CloudNoteViewHolder viewHolder, NoteItem noteItem, int position) {
                final String noteId = getRef(position).getKey();

                reference.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("timestamp")) {
                            String title = dataSnapshot.child("title").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();

                            viewHolder.setCloudNoteTitle(title);

                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            viewHolder.setCloudNoteTime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp), getContext()));

                            //make clickable
                            viewHolder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    //
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private void updateUI() {
        if (auth.getCurrentUser() != null) {
            Log.i("MainActivity", "fAuth != null");
        } else {
            Intent startIntent = new Intent(getContext(), MainActivity.class);
            startActivity(startIntent);
            Log.i("MainActivity", "fAuth == null");
        }
        }
    }
