package com.example.lwb.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lwb.Constants;
import com.example.lwb.R;
import com.example.lwb.VerificationAndValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class AuthorizationEmployeeFragment extends Fragment {

    private Button signIn;
    private Button btnBack;
    private TextInputEditText textInputEditLogin;
    private TextInputEditText textPassword;
    private TextInputLayout textInputLayoutLogin;
    private TextInputLayout textInputLayoutPassword;

    String login;
    String password;


    public AuthorizationEmployeeFragment() {
    }

    //нужный фрагменту интерфейс
    public interface AuthorizationEmployeeFragmentInterface {
        public void toMainActivityEmployees();
        public void toAuthorizationUsersFragment();
    }

    private AuthorizationEmployeeFragment.AuthorizationEmployeeFragmentInterface authorizationEmployeeFragmentInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorization_employee, container, false);
        //инициализация компонентов
        signIn = view.findViewById(R.id.buttonIn);
        btnBack= view.findViewById(R.id.buttonBack);
        textInputEditLogin = view.findViewById(R.id.textInputEmail);
        textPassword = view.findViewById(R.id.textInputPassword);
        textInputLayoutLogin = view.findViewById(R.id.outlinedTextField2);
        textInputLayoutPassword = view.findViewById(R.id.outlinedTextField3);
        //назначение слушателей
        btnBack.setOnClickListener(onCancelListener);
        signIn.setOnClickListener(onSignInListener);
        return view;
    }

    //ЖЦ выполняется когда фрагмент подсоединяется к активности
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthorizationEmployeeFragment.AuthorizationEmployeeFragmentInterface) {
            authorizationEmployeeFragmentInterface = (AuthorizationEmployeeFragment.AuthorizationEmployeeFragmentInterface) context;
        }

    }

    //Слушатели
    View.OnClickListener onCancelListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            authorizationEmployeeFragmentInterface.toAuthorizationUsersFragment();
        }
    };

    View.OnClickListener onSignInListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textInputLayoutPassword.setError(null);
            textInputLayoutLogin.setError(null);
            login = String.valueOf(textInputEditLogin.getText()).replaceAll("\\s+", "");
            password = String.valueOf(textPassword.getText()).replaceAll("\\s+", "");
            if (login.isEmpty() || password.isEmpty()) {

                if (login.isEmpty() && password.isEmpty()) {
                    textInputLayoutPassword.setError("Введите пароль");
                    textInputLayoutLogin.setError("Введите логин");
                }
                else {
                    if (login.isEmpty())
                        textInputLayoutLogin.setError("Введите логин");
                    else
                        textInputLayoutPassword.setError("Введите пароль");
                }
            }
            else {
                signIn(login, password);
            }
        }
    };

    //метод авторизации
    void signIn(String mail, String password) {
        //подключение к бд, обращение к коллекции "Администратор"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_ADMIN).document(mail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get(Constants.USER_NAME) !=null){
                    StringBuilder hash= VerificationAndValidation.getPassword(password);
                    //проверка совпадения логгина и пароля в бд
                    if (String.valueOf(hash).equals(documentSnapshot.get(Constants.USER_PASSWORD).toString())) {
                        SharedPreferences sharedPreferences= getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString(Constants.USER_NAME, Constants.COLLECTION_ADMIN).apply();
                        authorizationEmployeeFragmentInterface.toMainActivityEmployees();
                    }
                    else
                        Toast.makeText(getContext(),"Неверный пароль",Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(getContext(), "Такого пользователя не существеут", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Произошла ошибка, проверьте подключение к Интернету и попробуйте зановао!", Toast.LENGTH_LONG).show();

            }
        });
    }




}