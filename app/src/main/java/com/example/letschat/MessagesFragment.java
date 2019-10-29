package com.example.letschat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.letschat.adapters.MessagesAdapter;
import com.example.letschat.model.MessageModel;
import com.example.letschat.model.UserApi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MessagesFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MessagesFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MessagesFragment extends Fragment {
    public static final String TAG = "TAG";
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<MessageModel> modelArrayList;

    private RecyclerView recyclerView;
    private MessagesAdapter messagesAdapter;


    private UserApi userApi;

    public MessagesFragment(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        modelArrayList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerview_messages);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        userApi = UserApi.getInstance();

        databaseReference.child("userIds")
                .child(userApi.getUserId())
                .child("messages")
                .orderByChild("timeStamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelArrayList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Log.d(TAG, "onDataChange: " + ds.toString());
                            MessageModel messageModel = ds.getValue(MessageModel.class);
                            modelArrayList.add(messageModel);
                        }
                        Collections.reverse(modelArrayList);
                        messagesAdapter = new MessagesAdapter(context, modelArrayList);
                        recyclerView.setAdapter(messagesAdapter);
                        messagesAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        return view;
    }
}
