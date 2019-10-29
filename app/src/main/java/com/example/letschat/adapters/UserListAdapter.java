package com.example.letschat.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Chat;
import com.example.letschat.R;
import com.example.letschat.model.UserDetails;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    public static final String TAG = "TAG";

    private Context context;
    private ArrayList<UserDetails> userDetailsList;

    public UserListAdapter(Context context, ArrayList<UserDetails> userDetailsList) {
        this.context = context;
        this.userDetailsList = userDetailsList;
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder holder, int position) {
        UserDetails userDetails = userDetailsList.get(position);

        holder.username.setText(userDetails.getUsername());
    }

    @Override
    public int getItemCount() {
        return userDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public LinearLayout user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_row);
            user = itemView.findViewById(R.id.user_row);

            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Chat.class);
                    UserDetails userDetails = userDetailsList.get(getAdapterPosition());
                    intent.putExtra("userId", userDetails.getUserid());
                    intent.putExtra("username", userDetails.getUsername());
                    context.startActivity(intent);
                }
            });


        }
    }
}
