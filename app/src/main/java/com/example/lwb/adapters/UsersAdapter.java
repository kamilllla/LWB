package com.example.lwb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lwb.R;
import com.example.lwb.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
private List<User> users;

public interface UserInterface {
        void onClickUser(User user);

    }
    private final UsersAdapter.UserInterface interfaceClick;

    public UsersAdapter(List<User> users, UserInterface interfaceClick) {
        this.users = users;
        this.interfaceClick = interfaceClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName=itemView.findViewById(R.id.nameUser);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //создание из содержимого layout View-элемента
        View userView = inflater.inflate(R.layout.item_user_list, parent, false);
        UsersAdapter.ViewHolder viewHolder = new UsersAdapter.ViewHolder(userView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=users.get(position);
        // устанавливаем данные View на основе макета(содержашемся в holder)
       holder.textName.setText(user.name);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               interfaceClick.onClickUser(user);
           }
       });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}
