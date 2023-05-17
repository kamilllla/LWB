package com.example.lwb.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.lwb.Constants;
import com.example.lwb.DateTreatmentMethods;
import com.example.lwb.R;
import com.example.lwb.VerificationAndValidation;
import com.example.lwb.activities.EnterActivity;
import com.example.lwb.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class AccountFragment extends Fragment {

    Button saveChange;
    Button buttonCancelChange;
    Button buttonToBooksList;
    Button buttonChangeData;
    Button buttonLogOut;
    String login;
    String dateOfTheBookedEvent;
    String timeOfTheBookedEvent;

    TextView loginText;
    TextInputEditText passwordText;
    TextInputEditText numberText;
    TextInputLayout numberLayout;
    TextInputLayout passwordLayout;
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        saveChange=view.findViewById(R.id.saveChange);
        loginText=view.findViewById(R.id.inputLogin);
        passwordText=(TextInputEditText)view.findViewById(R.id.inputPasswordPersonalData);
        numberText=view.findViewById(R.id.inputNumber);
        passwordLayout=view.findViewById(R.id.outlinedPassword);
        numberLayout=view.findViewById(R.id.outlinedNumber);
        buttonToBooksList=view.findViewById(R.id.buttonToBookList);
        buttonCancelChange=view.findViewById(R.id.cancelChange);
        buttonChangeData=view.findViewById(R.id.changeData);
        buttonLogOut=view.findViewById(R.id.buttonLogOut);
        login=getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USER_NAME, Constants.USER_NAME);
        loadData(login);
        //getToken();
        saveChange.setOnClickListener(clickOnButton);
        buttonCancelChange.setOnClickListener(clickOnButton);
        buttonToBooksList.setOnClickListener(clickOnButton);
        buttonChangeData.setOnClickListener(clickOnButton);
        buttonLogOut.setOnClickListener(clickOnButton);
        //passwordText.setOnFocusChangeListener(focusChangeListener);
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
    //выгрузка номера телефона из БД- параметр - логин текущего пользоввателя
    private void loadNumber(String login){
        db.collection(Constants.COLLECTION_USERS).document(login).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                   numberText.setText(document.getString("number"));
                }
                else{
                    numberText.setText("Error");

                }
            }
        });
    }

    //функция приведения инттерфеса к изначальному виду, то есть для просмотра
    private void bringingInterfaceToOriginal(){
        passwordText.setText("******");
        numberText.setFocusable(false);
        passwordText.setFocusable(false);
        numberLayout.setEndIconVisible(false);
        passwordLayout.setEndIconVisible(false);
         buttonChangeData.setVisibility(View.VISIBLE);
        saveChange.setVisibility(View.GONE);
        buttonCancelChange.setVisibility(View.GONE);

    }



    //слушатель события клика
    private View.OnClickListener clickOnButton = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            numberLayout.setError(null);
            passwordLayout.setError(null);
            switch (v.getId()) {
                case R.id.saveChange:
                    try {
                        String number = String.valueOf(numberText.getText()).replaceAll("\\s", "");
                        String pswrd = String.valueOf(passwordText.getText()).replaceAll("\\s", "");
                        if (number.isEmpty() || pswrd.isEmpty()) {
                            if (number.isEmpty() && pswrd.isEmpty()) {
                                numberLayout.setError("Введите значения");
                                passwordLayout.setError("Введите значения");
                                return;
                            }
                            if (number.isEmpty()) {
                                numberLayout.setError("Введите номер телефона");
                                return;
                            }
                            if (pswrd.isEmpty()) {
                                passwordLayout.setError("Введите номер телефона");
                                return;
                            }
                        }
                        if (VerificationAndValidation.checkNumberIncorrect(number)) {
                            numberLayout.setError("Формат телефона должен быть:+7/8(9xx) xxx-xx-xx(знаки (,),-могут быть опущены)");
                            return;
                        }
                        if (pswrd.equals("******") && number.isEmpty() == false) {
                            final DocumentReference sfDocRef = db.collection(Constants.COLLECTION_USERS).document(login);
                            db.runTransaction(new Transaction.Function<Integer>() {
                                @Override
                                public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                    transaction.update(sfDocRef, "number", number);
                                    return 1;
                                }
                            });
                            bringingInterfaceToOriginal();
                            return;

                        }
                        if (pswrd.isEmpty() == false && number.isEmpty() == false) {
                            StringBuilder hash= VerificationAndValidation.getPassword(pswrd);
                            final DocumentReference sfDocRef = db.collection(Constants.COLLECTION_USERS).document(login);
                            db.runTransaction(new Transaction.Function<Integer>() {
                                @Override
                                public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                    transaction.update(sfDocRef, "number", number);
                                    transaction.update(sfDocRef, "password", String.valueOf(hash));
                                    return 1;
                                }
                            });
                           bringingInterfaceToOriginal();
                           return;

                        } else {
                            Toast.makeText(getContext(), "Данные не изменены", Toast.LENGTH_LONG).show();
                            bringingInterfaceToOriginal();
                            return;
                        }
                    }
                    catch (Exception e){
                       bringingInterfaceToOriginal();
                        Toast.makeText(getContext(), "Произошла ошибка изменения данных", Toast.LENGTH_LONG).show();
                        return;
                    }

                case R.id.buttonToBookList:
                    checkBookingExist();
                    break;
                case R.id.cancelChange:
                    loadNumber(login);
                    bringingInterfaceToOriginal();

                    break;
                case R.id.changeData:
                    passwordText.setFocusableInTouchMode(true);
                    passwordText.setFocusable(true);
                    numberText.setFocusable(true);
                    numberText.setFocusableInTouchMode(true);
                    numberText.requestFocus();
                    passwordLayout.setEndIconDrawable(R.drawable.ic_baseline_edit_24);
                    numberLayout.setEndIconDrawable(R.drawable.ic_baseline_edit_24);
                    numberLayout.setEndIconVisible(true);
                    passwordLayout.setEndIconVisible(true);
                    buttonChangeData.setVisibility(View.GONE);
                    saveChange.setVisibility(View.VISIBLE);
                    buttonCancelChange.setVisibility(View.VISIBLE);
                    break;
                case R.id.buttonLogOut:
                    signOut();
                    break;
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
//функция выхода из аккаунта
    private void signOut() {
        Intent intent = new Intent(getActivity(), EnterActivity.class);
        //getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().clear().apply();
        startActivity(intent);
    }



}