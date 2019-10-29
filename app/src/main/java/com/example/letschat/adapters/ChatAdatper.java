package com.example.letschat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.R;
import com.example.letschat.model.MessageModel;
import com.example.letschat.model.UserApi;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatAdatper extends RecyclerView.Adapter<ChatAdatper.ViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<MessageModel> messages;

    public ChatAdatper(Context context, ArrayList<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_LEFT){
            view = LayoutInflater.from(context).inflate(R.layout.recv_message_row, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.send_message_row, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        String username = message.getFrom();
        String msg = message.getMessage();
        long timestamp = message.getTimeStamp();
        Timestamp ts = new Timestamp(timestamp);
        Date date = new Date(ts.getTime());
        @SuppressLint("SimpleDateFormat") String formattedDate = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(date);

        if(username.equals(UserApi.getInstance().getUsername())){
            holder.username.setText("You");
        }else {
            holder.username.setText(username);
        }
        holder.message.setText(msg);
        holder.date.setText(formattedDate);

    }

    @Override
    public int getItemViewType(int position) {
        UserApi userApi = UserApi.getInstance();
        if(userApi.getUsername().equals(messages.get(position).getFrom())){
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, date, message;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.chat_row_username);
            date = itemView.findViewById(R.id.message_date);
            message = itemView.findViewById(R.id.chat_row_msg);
        }
    }
}
