package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letschat.adapters.UserListAdapter;
import com.example.letschat.model.UserApi;
import com.example.letschat.model.UserDetails;
import com.example.letschat.util.APISingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

//Not in use
public class UserList extends AppCompatActivity {
    public static final String TAG = "TAG";

    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;

    private ArrayList<UserDetails> userDetails = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent, null));

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.user_list_bottom_bar);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.chat_bottom_bar:
//                        startActivity(new Intent(UserList.this, MessagesActivity.class));

                }
                return true;
            }
        });


        String dbUrl = "https://letschat-72a11.firebaseio.com/users/usernames.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, dbUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    Iterator i = obj.keys();
                    String key = "";
                    UserApi userApi = UserApi.getInstance();

                    while(i.hasNext()){
                        key = i.next().toString();
                        if(!key.equals(userApi.getUsername())){
                            String email, userId, username;
                            email = obj.getJSONObject(key).getString("email");
                            userId = obj.getJSONObject(key).getString("userid");
                            username = obj.getJSONObject(key).getString("username");

                            userDetails.add(new UserDetails(email, username, userId));

                            Log.d(TAG, "onResponse: USER LIST " + obj.getJSONObject(key).get("email"));
                        }

                    }

                    Log.d(TAG, "onCreate: " + userDetails);
                    userListAdapter = new UserListAdapter(UserList.this, userDetails);
                    recyclerView.setAdapter(userListAdapter);

                    userListAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = APISingleton.getInstance(UserList.this).getRequestQueue();
        requestQueue.add(stringRequest);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
