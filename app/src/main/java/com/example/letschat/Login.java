package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letschat.model.UserApi;
import com.example.letschat.util.APISingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Login extends AppCompatActivity {
    public static final String TAG = " TAG";

    private EditText email, password;
    private Button login;
    private TextView register;
    private ProgressBar progressBar;

    private String em, pass;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        register = findViewById(R.id.register_login);
        login = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_login);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                em = email.getText().toString().trim();
                pass = password.getText().toString();

                if(TextUtils.isEmpty(em)){
                    email.setError("Can't be blank");
                }
                else if(TextUtils.isEmpty(pass)){
                    password.setError("Can't be blank");
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(em, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    currentUser = firebaseAuth.getCurrentUser();
                                    assert currentUser != null;
                                    final String userId = currentUser.getUid();
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
                                                progressBar.setVisibility(View.INVISIBLE);
                                                startActivity(new Intent(Login.this, MainActivity.class));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG, "onErrorResponse: ", error);
                                            progressBar.setVisibility(View.INVISIBLE);

                                        }
                                    });

                                    RequestQueue requestQueue = APISingleton.getInstance(Login.this).getRequestQueue();
                                    requestQueue.add(stringRequest);


                                } else {
                                    Toast.makeText(Login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: ", e);
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                }
            }
        });


    }
}
