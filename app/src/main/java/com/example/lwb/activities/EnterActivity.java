package com.example.lwb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.lwb.fragments.AuthorizationEmployeeFragment;
import com.example.lwb.fragments.AuthorizationFragment;
import com.example.lwb.R;
import com.example.lwb.fragments.RegistrationFragment;

public class EnterActivity extends AppCompatActivity implements AuthorizationFragment.AuthorizationFragmentInterface, RegistrationFragment.SecondFragmentInterface, AuthorizationEmployeeFragment.AuthorizationEmployeeFragmentInterface {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment=new AuthorizationFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter2);
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void toRegistrationFragmnet() {
        fragment=new RegistrationFragment();
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void toMainActivity() {
        Intent intent=new Intent(EnterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void toAuthEmployee() {
        fragment=new AuthorizationEmployeeFragment();
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void toActivity() {
        Intent intent=new Intent(EnterActivity.this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void toFirstFragment() {
        fragment=new AuthorizationFragment();
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void toMainActivityEmployees() {
        Intent intent=new Intent(EnterActivity.this, MainActivityForEmployee.class);
        startActivity(intent);

    }

    @Override
    public void toAuthorizationUsersFragment() {
        fragment=new AuthorizationFragment();
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }
}