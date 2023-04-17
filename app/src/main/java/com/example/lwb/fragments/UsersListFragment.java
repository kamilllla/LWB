package com.example.lwb.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lwb.Constants;
import com.example.lwb.R;
import com.example.lwb.User;
import com.example.lwb.adapters.UsersAdapter;
import com.example.lwb.activities.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;




public class UsersListFragment extends Fragment {


    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();;
    UsersAdapter guidAdapter;

    public UsersListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_users_list, container, false);
        RecyclerView recyclerView;

        UsersAdapter.UserInterface userInterface=new UsersAdapter.UserInterface() {
                @Override
           public void onClickUser(User user) {
                Intent intent=new Intent (getContext(), ChatActivity.class);
                intent.putExtra(Constants.USER,user);
                intent.putExtra(Constants.FLAG,false);
                startActivity(intent);
            }
        };



        recyclerView = view.findViewById(R.id.usersList);


        firebaseFirestore.collection(Constants.COLLECTION_USERS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<User> users = new ArrayList<User>();
                    for (QueryDocumentSnapshot document : task.getResult()) {


                        User user=new User();
                        user.name=document.getId();
                        users.add(user);

                    }
                    Log.i("TAG", users.get(1).name);
                    guidAdapter = new UsersAdapter(users, userInterface);
                    recyclerView.setAdapter(guidAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                }

                else {
                    Log.i("TAG", "Error:"+ task.getException() );
                }
            }
        });

        return view;
    }

}