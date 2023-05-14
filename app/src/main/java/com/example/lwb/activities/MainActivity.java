package com.example.lwb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.lwb.Event;
import com.example.lwb.Models.Booking;
import com.example.lwb.fragments.AccountFragment;
import com.example.lwb.Constants;
import com.example.lwb.fragments.BooksListFragment;
import com.example.lwb.fragments.CalendarFragment;
import com.example.lwb.fragments.CancelDialogFragment;
import com.example.lwb.fragments.EducationFragment;
import com.example.lwb.R;
import com.example.lwb.User;
import com.example.lwb.fragments.EventListFragment;
import com.example.lwb.fragments.RegistrationDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements CalendarFragment.CalendarFragmentInterface, EventListFragment.EventListFragmentInterface, RegistrationDialogFragment.RegistrationDialogInterface, AccountFragment.ProfileInterface,BooksListFragment.BooksListFragmentInterface, CancelDialogFragment.CancelDialogInterface{
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
                    return true;
                case R.id.event:
                    CalendarFragment fragment=new CalendarFragment();
                    loadFragment(fragment);
                    return true;


            }
            return  false;
        }
        private void loadFragment(Fragment fragment){
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.containers,fragment).commit();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.education);

    }

    @Override
    public void toEventListFragment(String date) {
        loadEventList(date);

    }

    @Override
    public void showDialogFragment(Event event) {
        RegistrationDialogFragment dialog = RegistrationDialogFragment.newInstance(event);
        dialog.show(getSupportFragmentManager(), "custom");

    }

    @Override
    public void showCalendarFragment() {
        CalendarFragment fragment=new CalendarFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containers,fragment).commit();

    }

    @Override
    public void updateEventList(String date) {
       loadEventList(date);


    }
    @Override
    public void showDialogFragmentForDeleting(Booking book) {
        CancelDialogFragment cancelDialogFragment=CancelDialogFragment.newInstance(book);
        cancelDialogFragment.show(getSupportFragmentManager(), "custom");
    }

    @Override
    public void showAccountFragment() {
        AccountFragment accountFragment=new AccountFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containers,accountFragment).commit();

    }
    @Override
    public void toBooksListFragment() {
        loadBookList();


    }

    @Override
    public void updateBookingList() {
        loadBookList();

    }

    public void loadBookList(){
        BooksListFragment fragment=new BooksListFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containers, fragment);
        fragmentTransaction.commit();

    }

    public void loadEventList(String date){

        EventListFragment fragment =EventListFragment.newInstance(date);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containers, fragment);
        fragmentTransaction.commit();

    }


}