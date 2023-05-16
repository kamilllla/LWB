package com.example.lwb.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lwb.Models.Message;
import com.example.lwb.R;

import java.util.List;

public class ChatAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Message> messageList;
    private String senderName;
    private static final int SENT_TYPE=1;
    private static final int RECEIVED_TYPE=2;

    public ChatAdapter(List<Message> messageList, String senderName) {
        this.messageList = messageList;
        this.senderName = senderName;
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getUserName().equals(senderName)){
            Log.i("TYPE", String.valueOf(SENT_TYPE));
            return SENT_TYPE;

        }
        else {
            Log.i("TYPE", String.valueOf(RECEIVED_TYPE));
            return RECEIVED_TYPE;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==SENT_TYPE){
            Log.i("TYPE", String.valueOf(SENT_TYPE));
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            //создание из содержимого layout View-элемента
            View userView = inflater.inflate(R.layout.message_item_sent, parent, false);
            ChatAdapter.SentViewHolder sentViewHolder = new ChatAdapter.SentViewHolder(userView);
            return sentViewHolder;

        }
        else{
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            //создание из содержимого layout View-элемента
            View userView = inflater.inflate(R.layout.message_item, parent, false);
            ChatAdapter.ReceivedViewHolder receivedViewHolder = new ChatAdapter.ReceivedViewHolder(userView);
            return receivedViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=messageList.get(position);
        if (getItemViewType(position)==SENT_TYPE){
            Log.i("SENT", message.getUserName());

            ((SentViewHolder) holder).textMessage.setText(message.getTextMessage());
            ((SentViewHolder) holder).textTime.setText(message.getMessageTime());
        }
        else{

            Log.i("REC", message.getUserName());
            ((ReceivedViewHolder) holder).textName.setText(message.getUserName());
            ((ReceivedViewHolder) holder).textMessage.setText(message.getTextMessage());
            ((ReceivedViewHolder) holder).textTime.setText(message.getMessageTime());


        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    static class SentViewHolder extends RecyclerView.ViewHolder {

        TextView textMessage;
        TextView textTime;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage=itemView.findViewById(R.id.message);
            textTime=itemView.findViewById(R.id.time);
        }

    }
    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textMessage;
        TextView textTime;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            textName=itemView.findViewById(R.id.nameUser);
            textMessage=itemView.findViewById(R.id.messageField);
            textTime=itemView.findViewById(R.id.timeOfMessage);
        }

    }

}
