package com.nonexistentware.cloudnotev1.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nonexistentware.cloudnotev1.Activity.EditCloudNoteActivity;
import com.nonexistentware.cloudnotev1.TimeUtil.GetTimeAgo;
import com.nonexistentware.cloudnotev1.Interface.ItemClickListener;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;
import com.nonexistentware.cloudnotev1.ViewHolder.CloudNoteViewHolder;

import java.util.ArrayList;
import java.util.Calendar;


public class CloudNoteFragment extends Fragment{

    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private DatabaseReference reference;
    private TextView noCloudItemTxt;
    private ArrayList<NoteItem> notes;

    FirebaseRecyclerOptions<NoteItem> options;
   public FirebaseRecyclerAdapter<NoteItem, CloudNoteViewHolder> adapter;


    private Calendar calendar;
    private String todayDate;
    private String currentTime;

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

        noCloudItemTxt = itemView.findViewById(R.id.noCloudItemText);

        notes = new ArrayList<>();

        calendar = Calendar.getInstance();
        todayDate = calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "Date: "+todayDate);
        currentTime = pad(calendar.get(Calendar.HOUR))+":"+pad(calendar.get(Calendar.MINUTE));
        Log.d("TIME", "Time: "+currentTime);

        loadUserData();
        loadDate();


        return itemView;
    }

    private String pad(int time) {
        if(time < 10)
            return "0"+time;
        return String.valueOf(time);

    }

    public void loadDate() {

        if (auth.getCurrentUser() != null) {
            reference = FirebaseDatabase.getInstance().getReference()
                    .child("CloudNote").child(auth.getCurrentUser().getUid());
        } else {
            reference = FirebaseDatabase.getInstance().getReference();
        }

        options = new FirebaseRecyclerOptions.Builder<NoteItem>()
                .setQuery(reference, NoteItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<NoteItem, CloudNoteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CloudNoteViewHolder viewHolder, int position, @NonNull NoteItem noteItem) {
                      final String noteId = getRef(position).getKey();

                      reference.child(noteId).addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("timestamp")) {
                                   String title = dataSnapshot.child("title").getValue().toString();
                                   String timestamp  = dataSnapshot.child("timestamp").getValue().toString();

                                   viewHolder.setCloudNoteTitle(title);

                                   GetTimeAgo getTimeAgo = new GetTimeAgo();
                                   viewHolder.setCloudNoteTime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp), getContext()));

                                   viewHolder.setItemClickListener(new ItemClickListener() {
                                       @Override
                                       public void onClick(View view, int position) {
                                           Intent intent = new Intent(getContext(), EditCloudNoteActivity.class);
                                           intent.putExtra("noteId", noteId);
                                           startActivity(intent);
                                       }
                                   });
                               }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });
            }

            @NonNull
            @Override
            public CloudNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cloud_layout_note_item, parent, false);
                return new CloudNoteViewHolder(itemView);
            }
        };
        adapter.startListening();
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
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }

}
