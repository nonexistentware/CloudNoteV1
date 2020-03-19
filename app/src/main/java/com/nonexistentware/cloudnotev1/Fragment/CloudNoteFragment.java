package com.nonexistentware.cloudnotev1.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nonexistentware.cloudnotev1.Activity.EditCloudNoteActivity;
import com.nonexistentware.cloudnotev1.Activity.MainActivity;
import com.nonexistentware.cloudnotev1.Adapter.NoteAdapter;
import com.nonexistentware.cloudnotev1.TimeUtil.GetTimeAgo;
import com.nonexistentware.cloudnotev1.Interface.ItemClickListener;
import com.nonexistentware.cloudnotev1.Model.NoteItem;
import com.nonexistentware.cloudnotev1.R;
import com.nonexistentware.cloudnotev1.ViewHolder.CloudNoteViewHolder;


public class CloudNoteFragment extends Fragment{

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    public ImageView removeBtn;
    private DatabaseReference reference;
    private TextView noCloudItemTxt;
    private EditText materialSearchBar;

    FirebaseRecyclerAdapter<NoteItem, CloudNoteViewHolder> adapter;

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
        database = FirebaseDatabase.getInstance();

        noCloudItemTxt = itemView.findViewById(R.id.noCloudItemText);

        if (auth.getCurrentUser() != null) {
//            reference = database.getReference().child(Common.STR_CLOUD_NOTE).child(auth.getCurrentUser().getUid());
            reference = FirebaseDatabase.getInstance().getReference()
                    .child("CloudNote").child(auth.getCurrentUser().getUid());
        }

        updateUI();
        loadDate();


        return itemView;
    }

    public void loadDate() {
        Query query = reference.orderByValue();
         adapter = new FirebaseRecyclerAdapter<NoteItem, CloudNoteViewHolder>(
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
                                    Intent intent = new Intent(getContext(), EditCloudNoteActivity.class);
                                    intent.putExtra("noteId", noteId);
                                    startActivity(intent);
                                }
                            });
//
//                           if (dataSnapshot.child(noteId).getValue() == null) {
//                               noCloudItemTxt.setVisibility(View.INVISIBLE);
//                           } else {
//                               noCloudItemTxt.setVisibility(View.VISIBLE);
//                           }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

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
