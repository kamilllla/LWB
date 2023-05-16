package com.example.lwb.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lwb.Constants;
import com.example.lwb.R;
import com.example.lwb.VerificationAndValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationFragment extends Fragment {


    Button buttonSignUp;
    Button buttonSignCancel;
    TextInputEditText textLogin;
    TextInputEditText textPassword;
    TextInputEditText textPassword2;
    TextInputEditText textNumber;
    private TextInputLayout textInputLayoutLogin;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutNumber;
    private TextInputLayout textInputLayoutPassword2;
    String login;
    String password;
    String repeatedPassword;
    String number;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    Map<String, Object> user=new HashMap<>();

    //нужный фрагменту интерфейс
    public interface SecondFragmentInterface {
        public void toActivity();
        public void toFirstFragment();
    }
    private SecondFragmentInterface secondFragmentInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_registration, container, false);

        buttonSignUp=view.findViewById(R.id.buttonUp);
        buttonSignCancel=view.findViewById(R.id.buttonCancel);

        textLogin=view.findViewById(R.id.textInputLogin);
        textPassword=view.findViewById(R.id.textInputPassword);
        textPassword2=view.findViewById(R.id.textInputPassword2);
        textNumber=view.findViewById(R.id.textInputNumber);

        textInputLayoutLogin = view.findViewById(R.id.outlinedLogin);
        textInputLayoutPassword = view.findViewById(R.id.outlinedPassword);
        textInputLayoutNumber = view.findViewById(R.id.outlinedNumber);
        textInputLayoutPassword2 = view.findViewById(R.id.outlinedPassword2);
        buttonSignCancel.setOnClickListener(clickOnButton);
        buttonSignUp.setOnClickListener(clickOnButton);


        return view;
    }

    //ЖЦ выполняется когда фрагмент подсоединяется к активности
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SecondFragmentInterface){
            secondFragmentInterface=(SecondFragmentInterface) context;
        }

    }


    //cлушатель события нажатия
    View.OnClickListener clickOnButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textInputLayoutPassword.setError(null);
            textInputLayoutLogin.setError(null);
            textInputLayoutPassword2.setError(null);
            textInputLayoutNumber.setError(null);

            login=String.valueOf(textLogin.getText()).replaceAll("\\s+", "");
            password=String.valueOf(textPassword.getText()).replaceAll("\\s+", "");
            repeatedPassword=String.valueOf(textPassword2.getText()).replaceAll("\\s+", "");
            number=String.valueOf(textNumber.getText()).replaceAll("\\s+", "");

            switch (v.getId()) {
                case R.id.buttonUp:

                    if (login.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty() || number.isEmpty()) {

                        if (login.isEmpty() && password.isEmpty()  && repeatedPassword.isEmpty() && number.isEmpty()) {

                            textInputLayoutPassword.setError("Введите пароль");
                            textInputLayoutLogin.setError("Введите логин");
                            textInputLayoutPassword2.setError("Введите пароль еще раз");
                            textInputLayoutNumber.setError("Введите номер телефона");
                        }
                        else {
                            if (password.isEmpty())
                                textInputLayoutPassword.setError("Введите пароль");
                            if (login.isEmpty())
                                textInputLayoutLogin.setError("Введите логин");
                            if (repeatedPassword.isEmpty())
                                textInputLayoutPassword2.setError("Введите пароль еще раз");
                            if (number.isEmpty())
                                textInputLayoutNumber.setError("Введите номер телефона");
                        }
                    }
                    else {


                        if (!password.equals(repeatedPassword)) {
                            textInputLayoutPassword.setError("Пароли не совпадают");
                            return;
                        }
                        if (VerificationAndValidation.checkNumberIncorrect(number)){
                            textInputLayoutNumber.setError("Формат телефона должен быть:+7/8(9xx) xxx-xx-xx(знаки (,),-могут быть опущены)");
                            return;
                        }

                            DocumentReference docRef = db.collection("Пользователи").document(login);
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.get("login") != null) {
                                        textInputLayoutLogin.setError("Такой пользователь уже существует");
                                    } else {
                                        StringBuilder hash= VerificationAndValidation.getPassword(password);
                                        user.put(Constants.USER_NAME, login);
                                        user.put(Constants.USER_PASSWORD, String.valueOf(hash));
                                        user.put(Constants.USER_NUMBER, number.replaceAll("[\\-\\(\\)]", ""));
                                        db.collection(Constants.COLLECTION_USERS).document(login).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("ADD", "successful add");
                                                    SharedPreferences sharedPreferences= getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                                                    sharedPreferences.edit().putString(Constants.USER_NAME, login).apply();
                                                    secondFragmentInterface.toActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                    }
                    break;

                case R.id.buttonCancel:
                    secondFragmentInterface.toFirstFragment();
                    break;
            }
        }

    };


}