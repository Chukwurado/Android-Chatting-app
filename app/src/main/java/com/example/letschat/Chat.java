package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.letschat.adapters.ChatAdatper;
import com.example.letschat.model.MessageModel;
import com.example.letschat.model.UserApi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Chat extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText messageText;
    private ImageButton sendButton;
    Toolbar toolbar;

    private String message, usernameTo, userIdTo, key;

    private RecyclerView recyclerView;
    private ChatAdatper chatAdatper;

    private ArrayList<MessageModel> messages = new ArrayList<>();

    private FirebaseDatabase database;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;

    private UserApi userApi = UserApi.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Change the color of the status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent, null));

        database = FirebaseDatabase.getInstance();
        databaseReference1 = database.getReference("messages");
        databaseReference2 = database.getReference("users");
        databaseReference3 = database.getReference("users");

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        usernameTo = bundle.getString("username");
        assert usernameTo != null;

        userIdTo = bundle.getString("userId");
        assert userIdTo != null;


        if(userApi.getUsername().compareTo(usernameTo) < 0){
            key = userApi.getUsername() + "_" + usernameTo;
        }
        else {
            key = usernameTo + "_" + userApi.getUsername();
        }
        Log.d(TAG, "onCreate: " + usernameTo);
        Log.d(TAG, "onCreate: " + userIdTo);

        toolbar = findViewById(R.id.toolbar_chat);
        toolbar.setTitle(usernameTo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSupportFragmentManager().popBackStackImmediate();
                finish();
//                startActivity(new Intent(Chat.this, MainActivity.class));
                //finish();
            }
        });

        messageText = findViewById(R.id.message_area);
        sendButton = findViewById(R.id.send_button);
        recyclerView = findViewById(R.id.recyclerview_chat);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        databaseReference1.child(key).orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: " + d);
                    MessageModel messageModel = d.getValue(MessageModel.class);
                    assert messageModel != null;
                    messages.add(messageModel);
                }

                chatAdatper = new ChatAdatper(Chat.this, messages);
                recyclerView.setAdapter(chatAdatper);
                recyclerView.scrollToPosition(chatAdatper.getItemCount()-1);
                chatAdatper.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = messageText.getText().toString();
                if(!TextUtils.isEmpty(message)){
                    messageText.setText("");
                    long time = new Date().getTime();
                    MessageModel messageModel = new MessageModel(userApi.getUsername(), usernameTo, message, time, userIdTo, userApi.getUserId());

                    databaseReference1.child(key).child(String.valueOf(time)).setValue(messageModel);

                    //Add messages collection to sender's collection
                    databaseReference2.child("userIds").child(userApi.getUserId()).child("messages").child(usernameTo).setValue(messageModel);

                    //Add messages collection to receiver's collection
                    databaseReference3.child("userIds").child(userIdTo).child("messages").child(userApi.getUsername()).setValue(messageModel);

                }
            }
        });



    }
}
