package com.example.lwb.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.lwb.adapters.ChatAdapter;
import com.example.lwb.Constants;
import com.example.lwb.Message;
import com.example.lwb.R;
import com.example.lwb.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {


    private User user;
    private List<Message> messages;
    private ChatAdapter chatAdapter;
    private String conversationId="";
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    TextInputEditText editText;
    TextView textView;
    FloatingActionButton buttonSend;
    Button buttonBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user=(User) getIntent().getSerializableExtra(Constants.USER);
        //инициализация компонетов активности
        textView=findViewById(R.id.nameReceiver);
        recyclerView=findViewById(R.id.listOfMesseges);
        editText=findViewById(R.id.messageField);
        buttonSend=findViewById(R.id.btnSend);
        buttonBack=findViewById(R.id.buttonBack);
        //назначение слушателей
        buttonBack.setOnClickListener(onClickBackListener);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        textView.setText(user.name);


        messages=new ArrayList<>();
        chatAdapter=new ChatAdapter(messages, getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME) );
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listenMessages();


    }
    //отправка сообщений
    private void sendMessage(){
        HashMap<String, Object> message=new HashMap<>();
        message.put(Constants.CHAT_SENDER, getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME));
        message.put(Constants.CHAT_MESSAGE,editText.getText().toString());
        message.put(Constants.CHAT_RECEIVER, user.name );
        message.put(Constants.CHAT_TIME, new Date() );
        firebaseFirestore.collection(Constants.COLLECTION_CHAT).add(message);
        if(!conversationId.equals("")) {
            updateConversation(editText.getText().toString() );
        }
        else{
            HashMap<String, Object> conversation=new HashMap<>();
            conversation.put(Constants.CHAT_SENDER,  getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME));
            conversation.put(Constants.CONVERSATION_SENDER_NAME, getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME));
            conversation.put(Constants.CONVERSATION_RECEIVER_NAME, user.name );
            conversation.put(Constants.CONVERSATION_LAST_MESSAGE, editText.getText().toString() );
            conversation.put(Constants.CHAT_TIME, new Date());
            addConversation(conversation);
        }
        editText.setText(null);
    }
//преобразование даты из бд в нудный формат
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("hh:mm a, dd.MM.yy", Locale.getDefault()).format(date);
    }

    //метод для выгрузки сообщений из БД
    private void listenMessages(){
        firebaseFirestore.collection(Constants.COLLECTION_CHAT).
                whereEqualTo(Constants.CHAT_SENDER,  getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME)).
                whereEqualTo(Constants.CHAT_RECEIVER,  user.name).
                addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.COLLECTION_CHAT).
                whereEqualTo(Constants.CHAT_SENDER,  user.name).
                whereEqualTo(Constants.CHAT_RECEIVER,  getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NAME,Constants.USER_NAME)).
                addSnapshotListener(eventListener);

    }


//слушатель для метода выгрузки сообщений из БД
    private final EventListener<QuerySnapshot> eventListener = (value, error) ->{
        if (error != null){
            return;
        }
        if (value != null) {
            int count = messages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType()==DocumentChange.Type.ADDED) {
                    Message message = new Message();
                    message.setUserName(documentChange.getDocument().getString(Constants.CHAT_SENDER));
                    Log.d("CHATSENDER", documentChange.getDocument().getString(Constants.CHAT_SENDER));
                    message.setReceiverId(documentChange.getDocument().getString(Constants.CHAT_RECEIVER));
                    message.setTextMessage(documentChange.getDocument().getString(Constants.CHAT_MESSAGE));
                    message.setMessageTime(getReadableDateTime(documentChange.getDocument().getDate(Constants.CHAT_TIME)));
                    message.setMessageTimeDate(documentChange.getDocument().getDate(Constants.CHAT_TIME));
                    messages.add(message);
                }

            }
            Collections.sort(messages, (obj1, obj2) -> obj1.getMessageTimeDate().compareTo(obj2.getMessageTimeDate()));
            if (count ==0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(messages.size(), messages.size());
                recyclerView.smoothScrollToPosition(messages.size()-1);
            }
        }
        if (conversationId.equals("")){
            checkForConversation();
        }

    };

    //слушатель для события возврата к предыдущей активности
    View.OnClickListener onClickBackListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(getIntent().getExtras().getBoolean(Constants.FLAG)){
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
            }
            else{
                startActivity(new Intent(ChatActivity.this, MainActivityForEmployee.class));
            }

            }
        };
    //TODO: rename functions:
    private void addConversation(HashMap<String, Object> conversation){
        firebaseFirestore.collection(Constants.COLLECTION_CONVERSATION)
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId =documentReference.getId());


    }
    private void updateConversation(String message){
        DocumentReference documentReference=firebaseFirestore.collection(Constants.COLLECTION_CONVERSATION)
                .document(conversationId);
        documentReference.update(Constants.CONVERSATION_LAST_MESSAGE, message,
                Constants.CHAT_TIME, new Date());


    }

    private void checkForConversation(){
        if (messages.size()!=0){
            checkForConversationRemotely(getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME),
                    user.name);
            checkForConversationRemotely(user.name,
                    getApplicationContext()
                    .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                    .getString(Constants.USER_NAME, Constants.USER_NAME));
        }
    }
    private void checkForConversationRemotely(String senderId, String receiverId){
        firebaseFirestore.collection(Constants.COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.CHAT_SENDER, senderId)
                .whereEqualTo(Constants.CHAT_RECEIVER,receiverId)
                .get()
                .addOnCompleteListener(conversationOnCOmpleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCOmpleteListener=task -> {
        if (task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            conversationId=documentSnapshot.getId();
        }
    };
}