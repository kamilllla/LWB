package com.example.lwb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.lwb.fragments.AccountFragment;
import com.example.lwb.Constants;
import com.example.lwb.fragments.EducationFragment;
import com.example.lwb.R;
import com.example.lwb.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    User receiver=new User();
    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener= new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.account:
                    AccountFragment accountFragment=new AccountFragment();
                    loadFragment(accountFragment);
                    return true;

                case  R.id.education:
                    EducationFragment educationFragment=new EducationFragment();
                    loadFragment(educationFragment);
                    return true;

                case R.id.chat:
                    receiver.name= Constants.COLLECTION_ADMIN;
                    Intent intent=new Intent (MainActivity.this, ChatActivity.class);
                    intent.putExtra(Constants.USER, receiver);
                    intent.putExtra(Constants.FLAG, true);
                    startActivity(intent);
//                    FirebaseFirestore fb=FirebaseFirestore.getInstance();
//                    fb.collection(Constants.COLLECTION_ADMIN).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    receiver.name = document.getId();
//
//                                }
//                                Intent intent=new Intent (MainActivity.this, ChatActivity.class);
//                                Log.d("REC", receiver.name);
//                                intent.putExtra(Constants.USER, receiver);
//                                startActivity(intent);
//                            }
//                        }
//                    });


            }
            return  false;
        }
        private void loadFragment(Fragment fragment){
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.containers,fragment).addToBackStack(null).commit();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        bottomNavigationView.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.account);

    }

}