package com.example.letschat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Chat;
import com.example.letschat.R;
import com.example.letschat.model.MessageModel;
import com.example.letschat.model.UserApi;
import com.example.letschat.model.UserDetails;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MessageModel> messageModels;

    public MessagesAdapter(Context context, ArrayList<MessageModel> messageModels) {
        this.context = context;
        this.messageModels = messageModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        String username;
        if(messageModel.getFrom().equals(UserApi.getInstance().getUsername())){
            username = messageModel.getTo();
        }
        else {
            username = messageModel.getFrom();
        }
        String msg = messageModel.getMessage();
        long timestamp = messageModel.getTimeStamp();
        Timestamp ts = new Timestamp(timestamp);
        Date date = new Date(ts.getTime());
        @SuppressLint("SimpleDateFormat") String formattedDate = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(date);

        holder.msgPreview.setText(msg);
        holder.user.setText(username);
        holder.date.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout messageRow;
        public TextView user, msgPreview, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.msg_username_row);
            msgPreview = itemView.findViewById(R.id.message_preview);
            date = itemView.findViewById(R.id.msg_date);
            messageRow = itemView.findViewById(R.id.message_row);

            messageRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Chat.class);
                    MessageModel messageModel = messageModels.get(getAdapterPosition());
                    String username, userId;
                    if(messageModel.getFrom().equals(UserApi.getInstance().getUsername())){
                        username = messageModel.getTo();
                        userId = messageModel.getUserToId();
                    }
                    else {
                        username = messageModel.getFrom();
                        userId = messageModel.getUserFromId();
                    }
                    intent.putExtra("username", username);
                    intent.putExtra("userId", userId);

                    context.startActivity(intent);
                }
            });
        }
    }
}
