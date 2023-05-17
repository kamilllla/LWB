package com.example.lwb.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lwb.Models.Categories;
import com.example.lwb.Constants;
import com.example.lwb.Models.Guid;
import com.example.lwb.R;
import com.example.lwb.activities.VPlayer;
import com.example.lwb.adapters.GuidAdapter;
import com.example.lwb.adapters.SecondAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class EducationFragment extends Fragment {



    ArrayList<Categories> categories = new ArrayList<Categories>();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    GuidAdapter guidAdapter;
    EditText searchView;
    ImageView searchImageView;

    public EducationFragment() {
        // Required empty public constructor
    }
    SecondAdapter.OnGuidClickListenerInterface onGuidClickListenerInterface=new SecondAdapter.OnGuidClickListenerInterface() {
        @Override
        public void onClickGuid(Guid guid, Categories category) {
            Intent intent=new Intent(getContext(), VPlayer.class);
            intent.putExtra("guid", guid.getName());
            intent.putExtra("category", category.getNameOfCategorie());
            startActivity(intent);

        }
    };




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_education, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        searchView = view.findViewById(R.id.searchView);
        searchImageView=view.findViewById(R.id.searchImage);
        recyclerView = view.findViewById(R.id.recyclerView);
        categories=new ArrayList<>();
        guidAdapter = new GuidAdapter(categories, getContext(), onGuidClickListenerInterface);
        recyclerView.setAdapter(guidAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //назначение слушателе компонентам
        searchImageView.setOnClickListener(searchClick);
        //метод - слушатель ввода текста
        searchView.addTextChangedListener(textWatcher);
        loadDataInRecyclerView();
        return view;

    }



//слушатель для события открытия и закрытия поисковой строки
    View.OnClickListener searchClick =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // для показа поисковой строки
            if (searchView.getVisibility()==View.GONE){
                searchView.setVisibility(View.VISIBLE);
                searchView.requestFocus();
                searchView.setSelected(true);
                searchView.startAnimation(animate(true));
                openKeyboard();
                searchImageView.setImageResource(R.drawable.cancel);
                searchImageView.setPadding(35,35,35,35);
            }
            // скрытие поисковой строк
            else {
                searchView.setText("");
                searchView.setVisibility(View.GONE);
                searchView.startAnimation(animate(false));
                searchView.setSelected(false);
                closeKeyboard();
                searchImageView.setImageResource(R.drawable.search);
                searchImageView.setPadding(0,0,0,0);
                guidAdapter = new GuidAdapter(categories, getContext(), onGuidClickListenerInterface);
                recyclerView.setAdapter(guidAdapter);
            }

        }
    };

    //метод для анимации поискового поля
    public ScaleAnimation animate(boolean flag){
        ScaleAnimation scaleAnimation;
        if (flag)
            scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 1f, 1f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 1.0f);
        else
            scaleAnimation=new ScaleAnimation(1.0f, 0.0f, 1f, 1f, Animation.ABSOLUTE, 1.0f, Animation.ABSOLUTE, 0.0f);
        scaleAnimation.setFillAfter(false);
        scaleAnimation.setDuration(400);
        return scaleAnimation;
    }

    //методы для закрытия и открытия клавиатуры
    public void closeKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);

    }
    public void openKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    //слушатель события изменения текста в компоненте
    TextWatcher textWatcher= new TextWatcher() {
        //событие, наступающее после изменения текста
        @Override
        public void afterTextChanged(Editable editable) {
            List<Categories> filteredList = new ArrayList<>();
            int found = 0;
            for (Categories category : categories) {
                List<Guid> guidsFiltred = new ArrayList<>();
                found = 0;
                for (Guid guid : category.getGuids()) {
                    if (guid.getName().toLowerCase().trim().contains(editable.toString().toLowerCase().trim())) {
                        guidsFiltred.add(guid);
                        found = 1;
                    }
                }
                if (found == 1) {
                    filteredList.add(new Categories(category.getNameOfCategorie(), guidsFiltred));
                    guidAdapter = new GuidAdapter(filteredList, getContext(), onGuidClickListenerInterface);
                    recyclerView.setAdapter(guidAdapter);
                }
            }
        }
        //необходимые слушателю события (до изменения текста и во время)
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
    };
    // метод - обращение к БД и выгрузка списка видеогидов
    private void loadDataInRecyclerView() {
        db.collection(Constants.COLLECTION_LECTURES).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ArrayList<Guid> guid = new ArrayList<Guid>();
                        db.collection(Constants.COLLECTION_LECTURES).document(document.getId()).collection(document.getId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if (task.isSuccessful()) {

                                     for (QueryDocumentSnapshot document1 : task.getResult()) {
                                         guid.add(new Guid(document1.getId(), document1.getString("uri")));

                                     }
                                     categories.add(new Categories(document.getId(), guid));
                                     guidAdapter = new GuidAdapter(categories, getContext(), onGuidClickListenerInterface);
                                     recyclerView.setAdapter(guidAdapter);
                                     recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                 }

                             }
                         }
                        );


                    }
                } else {
                    Log.i("TAG", "Error:" + task.getException());
                }
            }
        });
    }








}