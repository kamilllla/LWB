package com.example.lwb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.lwb.fragments.EducationFragment;
import com.example.lwb.R;
import com.example.lwb.fragments.UsersListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivityForEmployee extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_employee);
        //иницализация комопнентов активности
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        //назначение слушателя для компонента меню
        bottomNavigationView.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.chat);
    }
    //слушатель
        private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener= new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case  R.id.education:
                        EducationFragment educationFragment=new EducationFragment();
                        loadFragment(educationFragment);
                        return true;

                    case R.id.chat:
                        UsersListFragment usersListFragment=new UsersListFragment();
                        loadFragment(usersListFragment);
                        return true;

                }
                return  false;
            }
            //метод загрузки и отображения фрагмента
            private void loadFragment(Fragment fragment){
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containers,fragment).commit();

            }
        };


}