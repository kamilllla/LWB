package com.example.lwb.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.lwb.Constants;
import com.example.lwb.R;
import com.example.lwb.VerificationAndValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthorizationFragment extends Fragment {

    private Button signIn;
    private Button signUp;
    private TextInputEditText textInputEditLogin;
    private TextInputEditText textPassword;
    private TextInputLayout textInputLayoutLogin;
    private TextInputLayout textInputLayoutPassword;
    private TextView textViewToFragment;
    String login;
    String password;

    public AuthorizationFragment() {
    }

    //нужный фрагменту интерфейс
    public interface AuthorizationFragmentInterface {
        void toRegistrationFragmnet();

        void toMainActivity();

        void toAuthEmployee();
    }

    private AuthorizationFragmentInterface authorizationFragmentInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorization, container, false);
        signIn = view.findViewById(R.id.buttonIn);
        signUp = view.findViewById(R.id.buttonUp);
        textInputEditLogin = view.findViewById(R.id.textInputEmail);
        textPassword = view.findViewById(R.id.textInputPassword);
        textInputLayoutLogin = view.findViewById(R.id.outlinedTextField2);
        textInputLayoutPassword = view.findViewById(R.id.outlinedTextField3);
        textViewToFragment = view.findViewById(R.id.textAuthEmployee);

        signUp.setOnClickListener(clickListener);
        signIn.setOnClickListener(clickListener);
        textViewToFragment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                textViewToFragment.setTextColor(R.color.black);
                authorizationFragmentInterface.toAuthEmployee();
            }
        });
        return view;
    }


    //ЖЦ выполняется когда фрагмент подсоединяется к активности
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthorizationFragmentInterface) {
            authorizationFragmentInterface = (AuthorizationFragmentInterface) context;
        }

    }

    //слушатель нажатия
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//обработка события при авториазции
                case R.id.buttonIn:
                    textInputLayoutPassword.setError(null);
                    textInputLayoutLogin.setError(null);
                    login = String.valueOf(textInputEditLogin.getText()).replaceAll("\\s+", "");
                    password = String.valueOf(textPassword.getText()).replaceAll("\\s+", "");
                    if (login.isEmpty() || password.isEmpty()) {

                        if (login.isEmpty() && password.isEmpty()) {
                            textInputLayoutPassword.setError("Введите пароль");
                            textInputLayoutLogin.setError("Введите логин");
                        } else {
                            if (login.isEmpty())
                                textInputLayoutLogin.setError("Введите логин");
                            else
                                textInputLayoutPassword.setError("Введите пароль");
                        }
                    }
                    else {
                        //обращение к БД
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection(Constants.COLLECTION_USERS).document(login);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.get("login") != null) {
                                    StringBuilder hash = VerificationAndValidation.getPassword(password);
                                    if (String.valueOf(hash).equals(documentSnapshot.get("password"))) {
                                        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                                        sharedPreferences.edit().putString(Constants.USER_NAME, login).apply();
                                        authorizationFragmentInterface.toMainActivity();
                                    } else
                                        Toast.makeText(getContext(), "Неверный пароль", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Такого пользователя не существует", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Произошла ошибка, проверьте подключение к Интернету и попробуйте заново!", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                    break;
                //обработка события при регистрации
                case R.id.buttonUp:
                    authorizationFragmentInterface.toRegistrationFragmnet();
                    break;


            }
        }
    };
}