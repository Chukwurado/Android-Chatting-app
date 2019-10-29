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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letschat.adapters.UserListAdapter;
import com.example.letschat.model.UserApi;
import com.example.letschat.model.UserDetails;
import com.example.letschat.util.APISingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class ContactListFragment extends Fragment {
    public static final String TAG = "TAG";

    private Context context;

    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;

    private ArrayList<UserDetails> userDetails = new ArrayList<>();

    public ContactListFragment(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);


        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        String dbUrl = "https://letschat-72a11.firebaseio.com/users/usernames.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, dbUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    userDetails.clear();
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
                    userListAdapter = new UserListAdapter(context, userDetails);
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

        RequestQueue requestQueue = APISingleton.getInstance(context).getRequestQueue();
        requestQueue.add(stringRequest);

        return view;
    }
}