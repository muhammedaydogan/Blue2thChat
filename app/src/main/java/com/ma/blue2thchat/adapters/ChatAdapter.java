package com.ma.blue2thchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.blue2thchat.R;
import com.ma.blue2thchat.objects.Message;

import java.util.ArrayList;
import java.util.Calendar;

import static com.ma.blue2thchat.fragments.SecondFragment.avatarRes;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    static final int INCOMING_MESSAGE = 12;
    static final int OUTGOING_MESSAGE = 13;

    Context context;

    ArrayList<Message> messages;

    int avatarNo;
    int receiverAvatarNo;

    public ChatAdapter(Context context, ArrayList<Message> messages, int avatarNo, int receiverAvatarNo) {
        this.context = context;
        this.messages = messages;
        this.avatarNo = avatarNo;
        this.receiverAvatarNo = receiverAvatarNo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == INCOMING_MESSAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_incoming_message, parent, false);
        } if (viewType == OUTGOING_MESSAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_outgoing_message, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (getItemViewType(position)) {
            case INCOMING_MESSAGE:
                if (avatarNo >= 0)
                    if (avatarNo < avatarRes.length)
                        holder.avatar.setImageResource(avatarRes[avatarNo]);

            case OUTGOING_MESSAGE:
                if (receiverAvatarNo >= 0)
                    if (receiverAvatarNo < avatarRes.length)
                        holder.avatar.setImageResource(avatarRes[receiverAvatarNo]);

        }

        holder.message.setText(message.getMessage());

        Calendar calendar = Calendar.getInstance();
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        if (hour.length() == 1) hour = "0" + hour;
        String min = String.valueOf(calendar.get(Calendar.MINUTE));
        if (min.length() == 1) min = "0" + min;
        holder.timestamp.setText(String.format("%s:%s", hour, min));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isIncoming())
            return INCOMING_MESSAGE;
        else
            return OUTGOING_MESSAGE;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView message;
        public TextView timestamp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.avatar = itemView.findViewById(R.id.avatar);
            this.message = itemView.findViewById(R.id.message);
            this.timestamp = itemView.findViewById(R.id.time);
        }
    }
}
