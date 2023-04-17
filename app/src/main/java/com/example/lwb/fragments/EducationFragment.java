package com.example.lwb.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.lwb.Categories;
import com.example.lwb.Guid;
import com.example.lwb.adapters.GuidAdapter;
import com.example.lwb.R;
import com.example.lwb.adapters.SecondAdapter;
import com.example.lwb.activities.VPlayer;
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

    public EducationFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_education, container, false);
        EditText editText = view.findViewById(R.id.searchView);
        //метод - слушатель ввода текста
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

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
                    }
                }
                //
                // guidAdapter.setFilteredList(filteredList);
            }
        });

        SecondAdapter.OnGuidClickListenerInterface onGuidClickListenerInterface=new SecondAdapter.OnGuidClickListenerInterface() {
            @Override
            public void onClickGuid(Guid guid, Categories category) {
                Intent intent=new Intent(getContext(), VPlayer.class);
                intent.putExtra("guid", guid.getName());
                intent.putExtra("category", category.getNameOfCategorie());
                startActivity(intent);

            }
        };


        recyclerView = view.findViewById(R.id.recyclerView);
        db.collection("Видеогиды").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ArrayList<Guid> guid = new ArrayList<Guid>();
                        db.collection("Видеогиды").document(document.getId()).collection(document.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                           @Override
                                                                                                                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                               if (task.isSuccessful()) {

                                                                                                                                                   for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                                                                                                                       Log.i("TAG", document.getId()+"  "+document1.getId());
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
                }
                else {
                    Log.i("TAG", "Error:"+ task.getException() );
                }
            }
        });

        return view;

    }
}