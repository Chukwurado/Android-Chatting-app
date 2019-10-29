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
import com.example.letschat.model.UserDetails;
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

public class Register extends AppCompatActivity {
    public static final String TAG = " TAG";

    private EditText username, email, password, confirmPass;
    private Button register;
    private TextView login;
    private ProgressBar progressBar;

    private String user, em, pass, conPass;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username_register);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password_register);
        confirmPass = findViewById(R.id.confirm_password);
        login = findViewById(R.id.login_register);
        register = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progress_register);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };


        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user = username.getText().toString().trim();
                em = email.getText().toString().trim();
                pass = password.getText().toString();
                conPass = confirmPass.getText().toString();

                if(TextUtils.isEmpty(user)){
                    username.setError("Can't be blank");
                }
                else if(TextUtils.isEmpty(em)){
                    email.setError("Can't be blank");
                }
                else if(TextUtils.isEmpty(pass)){
                    password.setError("Can't be blank");
                }
                else if(TextUtils.isEmpty(conPass)){
                    confirmPass.setError("Can't be blank");
                }
                else if(!pass.equals(conPass)){
                    confirmPass.setError("Passwords don't match");
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);

                    String dbUrl = "https://letschat-72a11.firebaseio.com/users/usernames.json";

                    RequestQueue requestQueue = APISingleton.getInstance(Register.this).getRequestQueue();

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, dbUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("null")){
                                register();
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if(!obj.has(user)){
                                        register();
                                    } else {
                                        username.setError("Username already exists");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d(TAG, "onErrorResponse: Register:142", error);
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });

                    requestQueue.add(stringRequest);

                }
            }
        });

    }

    private void register() {
        firebaseAuth.createUserWithEmailAndPassword(em, pass)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        String userId = currentUser.getUid();
                        UserApi userApi = UserApi.getInstance();
                        userApi.setUserId(userId);
                        userApi.setUsername(user);

                        UserDetails userDetails = new UserDetails(em, user, userId);
                        usersRef.child("usernames").child(user).setValue(userDetails);
                        usersRef.child("userIds").child(userId).setValue(userDetails);

                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(Register.this, MainActivity.class));

                    }else{
                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ", e);
                }
            });
        progressBar.setVisibility(View.INVISIBLE);

    }
}
