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

import com.example.lwb.R;
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
    String password2;
    String number;


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


        View.OnClickListener clickOnButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()) {
                    case R.id.buttonUp:
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        if (textLogin.getText().length() == 0 || textPassword.getText().length() == 0 || textPassword2.getText().length() == 0 || textNumber.getText().length() == 0) {
                            if (textLogin.getText().length() ==0  && textPassword.getText().length() == 0 && textPassword2.getText().length() == 0 && textNumber.getText().length() == 0) {

                                textInputLayoutPassword.setError("Введите пароль");
                                textInputLayoutLogin.setError("Введите логин");
                                textInputLayoutPassword2.setError("Введите пароль еще раз");
                                textInputLayoutNumber.setError("Введите номер телефона");
                            }
                            else {
                                textInputLayoutPassword.setError("Введите пароль");
                                textInputLayoutLogin.setError("Введите логин");
                                textInputLayoutPassword2.setError("Введите пароль еще раз");
                                textInputLayoutNumber.setError("Введите номер телефона");
                            }
                        }
                        else {

                            login = textLogin.getText().toString();
                            password = textPassword.getText().toString();
                            password2 = textPassword2.getText().toString();
                            number = textNumber.getText().toString();
                            if (password.equals(password2)) {
                                DocumentReference docRef = db.collection("Пользователи").document(login);
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.get("login") != null) {
                                            textInputLayoutLogin.setError("Такой пользователь существует");
                                        } else {
                                            StringBuilder hash= new StringBuilder();
                                            MessageDigest messageDigest= null;
                                            try {
                                                messageDigest = MessageDigest.getInstance("SHA-1");
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            }
                                            byte[] bytes=messageDigest.digest(password.getBytes());
                                            for (byte b: bytes){
                                                hash.append(String.format("%02X",b));

                                            }
                                            user.put("login", login);
                                            user.put("password", String.valueOf(hash));
                                            user.put("number", number);
                                            db.collection("Пользователи").document(login).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("ADD", "successful add");
                                                        SharedPreferences sharedPreferences= getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                                                        sharedPreferences.edit().putString("login", login).apply();
                                                        secondFragmentInterface.toActivity();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                textInputLayoutPassword.setError("Пароли не совпадают");
                            }
                        }
                        break;

                    case R.id.buttonCancel:
                        secondFragmentInterface.toFirstFragment();
                        break;
                }
            }

        };


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


}