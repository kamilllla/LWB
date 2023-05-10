package com.example.lwb.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lwb.Constants;
import com.example.lwb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class AccountFragment extends Fragment {

    Button saveChange;
    Button next;
    String login;
    TextInputEditText loginText;
    TextInputEditText passwordText;
    TextInputEditText numberText;
    FirebaseFirestore db=FirebaseFirestore.getInstance();


    public static AccountFragment newInstance(String login) {
        AccountFragment profile=new AccountFragment();
        Bundle bundle=new Bundle();
        bundle.putString("LOGIN", login);
        profile.setArguments(bundle);
        return profile;
    }

    //нужный фрагменту интерфейс
    public interface ProfileInterface {

    }
    private  ProfileInterface profileInterface;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_account, container, false);
        saveChange=view.findViewById(R.id.saveChange);
        loginText=view.findViewById(R.id.inputLogin);
        passwordText=view.findViewById(R.id.inputPassword);
        numberText=view.findViewById(R.id.inputNumber);
        login=getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME);
        loadData(login);
        getToken();
        saveChange.setOnClickListener(clickOnButton);
        passwordText.setOnFocusChangeListener(focusChangeListener);
        numberText.setOnFocusChangeListener(focusChangeListener);
        return view;
    }
    private void updateToken(String token){
        db.collection(Constants.COLLECTION_USERS).document(getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME)).update(Constants.USER_TOKEN, token).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("TOKEN", "SUCCESS");

                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TOKEN", "FAILURE");

            }
        });
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }



    private void signOut(){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        HashMap<String, Object> updates=new HashMap<>();
        updates.put(Constants.USER_TOKEN, FieldValue.delete());
        firebaseFirestore.collection(Constants.COLLECTION_USERS).document(getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME)).
        update(updates).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().clear().apply();
                    }
                }
        ).
        addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Ошибка выхода из аккаунта", Toast.LENGTH_LONG).show();
            }
        });




    }
    //выгрузка личных данных из БД- параметр - логин текущего пользоввателя
    private void loadData(String login){
        db.collection(Constants.COLLECTION_USERS).document(login).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    loginText.setText(document.getString("login"));
                    numberText.setText(document.getString("number"));

                }
                else{
                    loginText.setText("Error");
                    numberText.setText("Error");

                }
            }
        });
    }

    //слушатель изменения фокуса
    View.OnFocusChangeListener focusChangeListener=new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            switch (view.getId()) {
                case R.id.inputPassword:
                    if (b) {
                        if (String.valueOf(passwordText.getText()).equals("******")) {
                            passwordText.setText("");
                        }
                    } else {
                        if (passwordText.getText().length() == 0) {
                            passwordText.setText("******");
                        }
                    }
                    break;
                case R.id.inputNumber:
                    break;
            }
            saveChange.setEnabled(true);
        }
    };



    //слушатель события клика
    View.OnClickListener clickOnButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.saveChange:
                    if (numberText.getText().toString().replaceAll("\\s", "").length()!=0 || passwordText.getText().toString().replaceAll("\\s", "").length()!=0) {
                        final DocumentReference sfDocRef = db.collection(Constants.COLLECTION_USERS).document(login);

                        db.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                                String number = String.valueOf(numberText.getText());
                                String pswrd=String.valueOf(passwordText.getText());
                                if (pswrd.equals("******")){
                                    transaction.update(sfDocRef, "number", number);
                                }
                                else {
                                    StringBuilder hash= new StringBuilder();
                                    MessageDigest messageDigest= null;
                                    try {
                                        messageDigest = MessageDigest.getInstance("SHA-1");
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                    byte[] bytes=messageDigest.digest(pswrd.getBytes());
                                    for (byte b: bytes){
                                        hash.append(String.format("%02X",b));

                                    }
                                    transaction.update(sfDocRef, "number", number);
                                    transaction.update(sfDocRef, "password", String.valueOf(hash));
                                }
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                passwordText.setText("******");
                                passwordText.clearFocus();
                                numberText.clearFocus();
                                Log.d("TAG", "Transaction success: ");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Transaction failure.", e);
                                    }
                                });
                    }
                    saveChange.setEnabled(false);
                    break;
            }
        }
    };


}