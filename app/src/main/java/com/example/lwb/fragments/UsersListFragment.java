package com.example.lwb.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lwb.Constants;
import com.example.lwb.Message;
import com.example.lwb.R;
import com.example.lwb.User;
import com.example.lwb.adapters.RecentConversationAdapter;
import com.example.lwb.activities.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class UsersListFragment extends Fragment {

//глобальные переменные
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private  RecentConversationAdapter recentConversationAdapter;
    List<Message> conversations;
    RecyclerView recyclerView;
    TextView textAlert;

//требуемый пустой конструктор
    public UsersListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    RecentConversationAdapter.UserInterface userInterface=new RecentConversationAdapter.UserInterface() {
        @Override
        public void onClickUser(User user) {
            Intent intent=new Intent (getContext(), ChatActivity.class);
            intent.putExtra(Constants.USER, user);
            intent.putExtra(Constants.FLAG,false);
            startActivity(intent);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_users_list, container, false);
        recyclerView=view.findViewById(R.id.usersList);
        textAlert=view.findViewById(R.id.alert);
        conversations=new ArrayList<>();
        recentConversationAdapter=new RecentConversationAdapter(conversations, userInterface);
        recyclerView.setAdapter(recentConversationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listenConversations();
        return view;
    }


    private void listenConversations(){
        firebaseFirestore.collection(Constants.COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.CHAT_SENDER, getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME))
    .addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.CHAT_RECEIVER, getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME))
                .addSnapshotListener(eventListener);

    }
    private final EventListener<QuerySnapshot> eventListener= (value, error) ->{
        if (error != null) {
            return;
        }
        if(value!=null){

            for (DocumentChange documentChange: value.getDocumentChanges()){
                if (documentChange.getType()==DocumentChange.Type.ADDED){
                    String senderId=documentChange.getDocument().getString(Constants.CHAT_SENDER);
                    String receiverId=documentChange.getDocument().getString(Constants.CHAT_RECEIVER);
                    Message message=new Message();
                    message.setUserName(senderId);
                    message.setReceiverId(receiverId);
                    if (getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME).equals(senderId)){
                        message.setCoversitionName(documentChange.getDocument().getString(Constants.CONVERSATION_RECEIVER_NAME));
                        message.setConversionId(documentChange.getDocument().getString(Constants.CONVERSATION_RECEIVER_NAME));
                    }
                    else
                    {
                        message.setCoversitionName(documentChange.getDocument().getString(Constants.CONVERSATION_SENDER_NAME));
                        message.setConversionId(documentChange.getDocument().getString(Constants.CONVERSATION_SENDER_NAME));
                    }
                    message.setTextMessage(documentChange.getDocument().getString(Constants.CONVERSATION_LAST_MESSAGE));
                    message.setMessageTimeDate(documentChange.getDocument().getDate(Constants.CHAT_TIME) );
                   conversations.add(message);

                }
                else {
                    if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        for (int i=0; i<conversations.size(); i++ ){
                             String senderId=documentChange.getDocument().getString(Constants.CHAT_SENDER);
                            String receiverId=documentChange.getDocument().getString(Constants.CHAT_RECEIVER);
                             if(conversations.get(i).getUserName().equals(senderId) && conversations.get(i).getReceiverId().equals(receiverId)){
                                 conversations.get(i).setTextMessage(documentChange.getDocument().getString(Constants.CONVERSATION_LAST_MESSAGE));
                                 conversations.get(i).setMessageTimeDate(documentChange.getDocument().getDate(Constants.CHAT_TIME));
                                 break;

                             }

                        }
                    }
                }
            }
            if (conversations.size()!=0) {
                Collections.sort(conversations, (obj1, obj2) -> obj2.getMessageTimeDate().compareTo(obj1.getMessageTimeDate()));
                recentConversationAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                recyclerView.setVisibility(View.VISIBLE);
                textAlert.setVisibility(View.GONE);
            }
            else {
                recyclerView.setVisibility(View.GONE);
                textAlert.setVisibility(View.VISIBLE);

            }



        }


    };

}