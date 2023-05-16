package com.example.lwb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lwb.R;
import com.example.lwb.Models.Test;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView viewQuestion;
    int i=0;
    String category;
    String guid;
    int currentResult=0;
    Button buttonFirst;
    Button buttonSecond;
    Button buttonCancel;
    Button repatButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        viewQuestion = findViewById(R.id.question);
        buttonFirst=findViewById(R.id.button);
        buttonSecond=findViewById(R.id.button2);
        buttonCancel=findViewById(R.id.buttonCancel);
        repatButton=findViewById(R.id.buttonToRepeat);
        TextView themeOfTest=findViewById(R.id.textViewHeader);
        category = getIntent().getStringExtra("category");
        guid = getIntent().getStringExtra("guid");
        sharedPreferences=getSharedPreferences("settings", MODE_PRIVATE);
        sharedPreferences.edit().putInt(guid, currentResult).apply();
        themeOfTest.setText(guid);



        db.collection("Видеогиды").document(category).collection(category).document(guid).collection("Вопросы")
                .document("1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        viewQuestion.setText(task.getResult().get("вопрос").toString());
                    }
                    catch(Exception e){
                        Toast.makeText(getApplicationContext(), "К сожалению по данному видеогиду нет доступных тестов", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(TestActivity.this, VPlayer.class);
                        intent.putExtra("guid", guid);
                        intent.putExtra("category", category);
                        startActivity(intent);


                    }
                }
                else {
                    Log.i("ERROR", "errror");
                }
            }
        });
        Log.i("CAT", category);
        Log.i("GUI", guid);


        buttonFirst.setOnClickListener(onClickListener);
        buttonSecond.setOnClickListener(onClickListener);
        buttonCancel.setOnClickListener(onClickListener);
        repatButton.setOnClickListener(onClickListener);



    }
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
                                                                                                                                                                        buttonFirst.setVisibility(View.GONE);
                                                                                                                                                                        buttonSecond.setVisibility(View.GONE);
                                                                                                                                                                        repatButton.setVisibility(View.VISIBLE);
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
                                                                                                                                                                        buttonFirst.setVisibility(View.GONE);
                                                                                                                                                                        buttonSecond.setVisibility(View.GONE);
                                                                                                                                                                        repatButton.setVisibility(View.VISIBLE);
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
                case R.id.buttonCancel:
                    Intent intent=new Intent(TestActivity.this, VPlayer.class);
                    intent.putExtra("guid", guid);
                    intent.putExtra("category", category);
                    startActivity(intent);
                    break;
                case R.id.buttonToRepeat:
                    Intent intentRepeat=new Intent(TestActivity.this,TestActivity.class);
                    intentRepeat.putExtra("guid", guid);
                    intentRepeat.putExtra("category", category);
                    startActivity(intentRepeat);
                    break;

            }
        }
    };





}
