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
import com.example.lwb.DateTreatmentMethods;
import com.example.lwb.Event;
import com.example.lwb.Models.Booking;
import com.example.lwb.R;
import com.example.lwb.VerificationAndValidation;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class AccountFragment extends Fragment {

    Button saveChange;
    Button buttonToBooksList;
    String login;
    String dateOfTheBookedEvent;
    String timeOfTheBookedEvent;

    TextInputEditText loginText;
    TextInputEditText passwordText;
    TextInputEditText numberText;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private  ProfileInterface profileInterface;


    public static AccountFragment newInstance(String login) {
        AccountFragment profile=new AccountFragment();
        Bundle bundle=new Bundle();
        bundle.putString("LOGIN", login);
        profile.setArguments(bundle);
        return profile;
    }

    //нужный фрагменту интерфейс
    public interface ProfileInterface {
        void toBooksListFragment();


    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_account, container, false);
        saveChange=view.findViewById(R.id.saveChange);
        loginText=view.findViewById(R.id.inputLogin);
        passwordText=view.findViewById(R.id.inputPassword);
        numberText=view.findViewById(R.id.inputNumber);
        buttonToBooksList=view.findViewById(R.id.buttonToBookList);
        login=getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME);
        loadData(login);
        getToken();
        saveChange.setOnClickListener(clickOnButton);
        buttonToBooksList.setOnClickListener(clickOnButton);
        passwordText.setOnFocusChangeListener(focusChangeListener);
        numberText.setOnFocusChangeListener(focusChangeListener);
        return view;
    }

    //событие прикремления фрагмента к контейнеру
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccountFragment.ProfileInterface) {
            profileInterface = (AccountFragment.ProfileInterface) context;
        }

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
                    if (numberText.getText().toString().replaceAll("\\s", "").length()!=0 ||
                    passwordText.getText().toString().replaceAll("\\s", "").length()!=0) {
                        final DocumentReference sfDocRef = db.collection(Constants.COLLECTION_USERS).document(login);
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                String number = String.valueOf(numberText.getText());
                                String pswrd=String.valueOf(passwordText.getText());
                                if (pswrd.equals("******")){
                                    transaction.update(sfDocRef, "number", number);
                                }
                                else {
                                    StringBuilder hash= VerificationAndValidation.getPassword(pswrd);
                                    transaction.update(sfDocRef, "number", number);
                                    transaction.update(sfDocRef, "password", String.valueOf(hash));
                                }
                                return null;
                            }
                        })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                case R.id.buttonToBookList:
                    checkBookingExist();

            }
        }
    };

//проверка наличия бронирований и переход к форме со списком
private void checkBookingExist(){
    List<String> bookingPersonalList=new ArrayList<>();
    db.collection(Constants.COLLECTION_USERS).document(login)
            .collection(Constants.BOOKING).get()
            //слушатель при событии успешного подключения к бд
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot document1 : queryDocumentSnapshots.getDocuments()){
                        //получения даты и времения конкретного брогиварония
                        dateOfTheBookedEvent=document1.getString(Constants.BOOKING_EVENT_DATE);
                        timeOfTheBookedEvent=document1.getString(Constants.BOOKING_EVENT_TIME);
                        //преобразование полученных данных
                        Date dateAndTimeOfEvent= DateTreatmentMethods.getDateFromStrings(timeOfTheBookedEvent,dateOfTheBookedEvent);
                        //проверка актуальности даты и времени забронированного события
                        if (DateTreatmentMethods.checkRelevanceOfTime(dateAndTimeOfEvent)) {
                            bookingPersonalList.add(document1.getId());
                        }
                    }
                    //проверка, есть ли доступные бронирования
                    if (bookingPersonalList.size()>0) {
                       profileInterface.toBooksListFragment();//переход на форму со списком активностей
                        Log.e("TAG", " NOT EMpTYl ist");
                    }
                    else {
                        Log.e("TAG", "EMpTYl ist");
                        Toast.makeText(getContext(), getText(R.string.alert_inaccessibility_books), Toast.LENGTH_LONG).show();

                    }
                }
            })
            //слушатель при событии невозомжного обращения к бд
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), getText(R.string.alert_inaccessibility_books), Toast.LENGTH_LONG).show();

                }
            });
}



    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
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


    private void signOut() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.USER_TOKEN, FieldValue.delete());
        firebaseFirestore.collection(Constants.COLLECTION_USERS)
        .document(getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME))
        .update(updates).addOnSuccessListener(
        new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().clear().apply();
            }
        }).
        addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Ошибка выхода из аккаунта", Toast.LENGTH_LONG).show();
            }
        });
    }



}