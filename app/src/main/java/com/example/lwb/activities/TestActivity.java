package com.example.lwb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lwb.R;
import com.example.lwb.Test;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView viewQuestion;
    int i=0;
    int currentResult=0;
    Button buttonFirst;
    Button buttonSecond;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        viewQuestion = findViewById(R.id.question);
        buttonFirst=findViewById(R.id.button);
        buttonSecond=findViewById(R.id.button2);
        String category = getIntent().getStringExtra("category");
        String guid = getIntent().getStringExtra("guid");
        sharedPreferences=getSharedPreferences("settings", MODE_PRIVATE);
        sharedPreferences.edit().putInt(guid, currentResult).apply();


        db = FirebaseFirestore.getInstance();
        db.collection("Видеогиды").document(category).collection(category).document(guid).collection("Вопросы").document("1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    viewQuestion.setText(task.getResult().get("вопрос").toString());
                }
                else {
                    Log.i("ERROR", "errror");
                }
            }
        });
        Log.i("CAT", category);
        Log.i("GUI", guid);

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId())
                {
                    case R.id.button:
                        db.collection("Видеогиды").document(category).collection(category).document(guid).collection("Вопросы").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                                        if (task.isSuccessful()) {
                                                                                                                                                                            ArrayList<Test> tests = new ArrayList<Test>();
                                                                                                                                                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                                                                                                                                                tests.add(new Test(document.get("вопрос").toString(),document.get("по").toString()));

                                                                                                                                                                            }
                                                                                                                                                                            Log.i("I", Integer.toString(i));
                                                                                                                                                                            if(buttonFirst.getText().toString().toLowerCase().equals(tests.get(i).getRightAnswer().toLowerCase())){
                                                                                                                                                                                currentResult=sharedPreferences.getInt(guid, 1000);
                                                                                                                                                                                Log.i("RES", Integer.toString(currentResult));
                                                                                                                                                                                currentResult++;
                                                                                                                                                                                sharedPreferences.edit().putInt(guid, currentResult).apply();
                                                                                                                                                                            }
                                                                                                                                                                            i++;
                                                                                                                                                                            if (i<tests.size()) {
                                                                                                                                                                                viewQuestion.setText(tests.get(i).getQuestion());
                                                                                                                                                                            }
                                                                                                                                                                            else{
                                                                                                                                                                                currentResult=sharedPreferences.getInt(guid, 1000);
                                                                                                                                                                                int maxResult=sharedPreferences.getInt("max_"+guid, 0);
                                                                                                                                                                                if (maxResult<currentResult){
                                                                                                                                                                                    sharedPreferences.edit().putInt("max_"+guid, currentResult).apply();

                                                                                                                                                                                }
                                                                                                                                                                                buttonFirst.setEnabled(false);
                                                                                                                                                                                buttonSecond.setEnabled(false);
                                                                                                                                                                                viewQuestion.setText("Ваш текущий результат - "+ currentResult+"\nВаш максимальный результат - "+ maxResult);

                                                                                                                                                                            }

                                                                                                                                                                        }
                                                                                                                                                                        else {
                                                                                                                                                                            Log.i("ERROR", "errror");
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                }
                            );
                        break;

                    case R.id.button2:
                        db.collection("Видеогиды").document(category).collection(category).document(guid).collection("Вопросы").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                                                @Override
                                                                                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                                        ArrayList<Test> tests = new ArrayList<Test>();
                                                                                                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                                                                                                                                            tests.add(new Test(document.get("вопрос").toString(),document.get("по").toString()));

                                                                                                                                                                        }
                                                                                                                                                                        Log.i("I", Integer.toString(i));
                                                                                                                                                                        if(buttonSecond.getText().toString().toLowerCase().equals(tests.get(i).getRightAnswer().toLowerCase())){
                                                                                                                                                                            currentResult=sharedPreferences.getInt(guid, 1000);
                                                                                                                                                                            Log.i("RES", Integer.toString(currentResult));
                                                                                                                                                                            currentResult++;
                                                                                                                                                                            sharedPreferences.edit().putInt(guid, currentResult).apply();
                                                                                                                                                                        }
                                                                                                                                                                        i++;
                                                                                                                                                                        if (i<tests.size()) {
                                                                                                                                                                            viewQuestion.setText(tests.get(i).getQuestion());
                                                                                                                                                                        }
                                                                                                                                                                        else{
                                                                                                                                                                            currentResult=sharedPreferences.getInt(guid, 1000);
                                                                                                                                                                            int maxResult=sharedPreferences.getInt("max_"+guid, 0);

                                                                                                                                                                            if (maxResult<currentResult){

                                                                                                                                                                                sharedPreferences.edit().putInt("max_"+guid, currentResult).apply();
                                                                                                                                                                            }
                                                                                                                                                                            Log.i("RES", "hfjfgjhgjhg");
                                                                                                                                                                            buttonFirst.setEnabled(false);
                                                                                                                                                                            buttonSecond.setEnabled(false);
                                                                                                                                                                            viewQuestion.setText("Ваш текущий результат - "+ currentResult+"\nВаш максимальный результат - "+ maxResult);


                                                                                                                                                                        }

                                                                                                                                                                    }
                                                                                                                                                                    else {
                                                                                                                                                                        Log.i("ERROR", "errror");
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }
                        );
                        break;



                }
            }
        };

        buttonFirst.setOnClickListener(onClickListener);
        buttonSecond.setOnClickListener(onClickListener);



    }




}
