package com.example.lwb.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

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
        textViewToFragment=view.findViewById(R.id.textAuthEmployee);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizationFragmentInterface.toRegistrationFragmnet();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayoutPassword.setError(null);
                textInputLayoutLogin.setError(null);
                login= String.valueOf(textInputEditLogin.getText());
                password=String.valueOf(textPassword.getText());
                if (textInputEditLogin.getText().length() == 0 || textPassword.getText().length() == 0) {

                    if (textInputEditLogin.getText().length() == 0 && textPassword.getText().length() == 0) {
                        textInputLayoutPassword.setError("Введите пароль");
                        textInputLayoutLogin.setError("Введите логин");
                    }
                    else {
                        if (textInputEditLogin.getText().length() == 0) {
                            textInputLayoutLogin.setError("Введите логин");
                        } else {
                            textInputLayoutPassword.setError("Введите пароль");
                        }
                    }
                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection(Constants.COLLECTION_USERS).document(login);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.get("login")!=null){
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
                                if (String.valueOf(hash).equals(documentSnapshot.get("password"))) {
                                    SharedPreferences sharedPreferences= getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                                    sharedPreferences.edit().putString("login", login).apply();
                                    authorizationFragmentInterface.toMainActivity();
                                }
                                else {
                                    Toast.makeText(getContext(),"Неверный пароль",Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Такого пользователя не существеут", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
//
                    //firstFragmentInterface.toMainActivity();
                }

            }
        });
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
}