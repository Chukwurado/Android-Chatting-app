package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letschat.model.UserApi;
import com.example.letschat.util.APISingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "tag";
    private FragmentManager fragmentManager;
    private Fragment fragment;
    String userId;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent, null));

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){
                    userId = currentUser.getUid();
                    String dbUrl = "https://letschat-72a11.firebaseio.com/users/userIds/" + userId + "/.json";

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, dbUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                UserApi userApi = UserApi.getInstance();
                                userApi.setUserId(userId);
                                userApi.setUsername(jsonObject.getString("username"));

                                Log.d(TAG, "onResponse: " +userApi);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: ", error);

                        }
                    });

                    RequestQueue requestQueue = APISingleton.getInstance(MainActivity.this).getRequestQueue();
                    requestQueue.add(stringRequest);
                }
                else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }
        };

        if(savedInstanceState == null){
            fragmentManager = getSupportFragmentManager();
            fragment = fragmentManager.findFragmentById(R.id.container);
            if(fragment == null){
                fragment = new ContactListFragment(MainActivity.this);
                fragmentManager.beginTransaction().replace(R.id.container, fragment)
                        .commit();
            }
        }



        BottomNavigationView bottomNavigationView = findViewById(R.id.user_list_bottom_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.chat_bottom_bar:
                        fragment = new MessagesFragment(MainActivity.this);
                        fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment).addToBackStack(null)
                            .commit();
                        break;

                    case R.id.users_bottom_bar:
                        fragment = new ContactListFragment(MainActivity.this);
                        fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment).addToBackStack(null)
                            .commit();

                        break;

                    case R.id.signout_bottom_bar:
                        firebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, Login.class));

                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
