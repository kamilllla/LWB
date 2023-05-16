package com.example.lwb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lwb.Models.Message;
import com.example.lwb.R;
import com.example.lwb.Models.User;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ViewHolder> {
private List<Message> messages;
//интерфейс для реализации метода при нажатиии на элемент списка
public interface UserInterface {
        void onClickUser(User uesr);

    }
    private final RecentConversationAdapter.UserInterface interfaceClick;
//коснтруктор
    public RecentConversationAdapter(List<Message> messages, UserInterface interfaceClick) {
        this.messages = messages;
        this.interfaceClick = interfaceClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView recentMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName=itemView.findViewById(R.id.nameUser);
            recentMessage=itemView.findViewById(R.id.recentMessage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //создание из содержимого layout View-элемента
        View userView = inflater.inflate(R.layout.item_user_list, parent, false);
        RecentConversationAdapter.ViewHolder viewHolder = new RecentConversationAdapter.ViewHolder(userView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message=messages.get(position);
        // устанавливаем данные View на основе макета(содержашемся в holder)
       holder.textName.setText(message.getCoversitionName());
       holder.recentMessage.setText(message.getTextMessage());
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               User user=new User();
               user.name=message.getCoversitionName();

               interfaceClick.onClickUser(user);
           }
       });


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


}
