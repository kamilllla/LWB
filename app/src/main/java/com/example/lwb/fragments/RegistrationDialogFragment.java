package com.example.lwb.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lwb.Constants;
import com.example.lwb.Event;
import com.example.lwb.JavaMailAPI;
import com.example.lwb.Models.Booking;
import com.example.lwb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class RegistrationDialogFragment extends DialogFragment {
    TextInputEditText mailEditText;
    TextInputEditText nameEditText;
    TextInputEditText surnameEditText;
    TextInputEditText patronomycEditText;
    TextInputEditText numberEditText;
    NumberPicker numberPicker;
    Bundle arguments;
    String name;
    String surname;
    String patronomyc;
    String number;
    String email;
    int count;
    JavaMailAPI javaMailAPI;

    private static final String ARG_NAME = "name";
    private static final String ARG_DESC = "description";
    private static final String ARG_TIME = "time";
    private static final String ARG_COUNT = "count";
    private static final String ARG_PLACE = "place";
    private static final String ARG_DATE = "date";




    public RegistrationDialogFragment() {
        // Required empty public constructor
    }

    //пвсевдоконструктор для передачи информации о выбранном событии
    public static RegistrationDialogFragment newInstance(Event event) {
        RegistrationDialogFragment fragment=new RegistrationDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putString(ARG_NAME,event.getName());
        bundle.putString(ARG_DESC, event.getDescription());
        bundle.putString(ARG_TIME,event.getTime());
        bundle.putString(ARG_DATE,event.getDate());
        bundle.putString(ARG_PLACE,event.getPlace());
        bundle.putInt(ARG_COUNT,event.getCount());
        fragment.setArguments(bundle);
        return fragment;
    }




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_registration_dialog, container, false);

        arguments=getArguments();



        Button cancel = view.findViewById(R.id.buttonCancel);
        Button bookButton = view.findViewById(R.id.buttonBook);
        numberPicker = view.findViewById(R.id.numberPicker);
        mailEditText=view.findViewById(R.id.textInputMail);
        nameEditText=view.findViewById(R.id.textInputName);
        surnameEditText=view.findViewById(R.id.textInputSurname);
        patronomycEditText=view.findViewById(R.id.textInputPatronomyc);
        numberEditText=view.findViewById(R.id.textInputNumber);
        customizeNumberPicker(arguments.getInt(ARG_COUNT));
        bookButton.setOnClickListener(buttonClick);
        cancel.setOnClickListener(buttonClick);
        return  view;
    }


    View.OnClickListener buttonClick =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId()==R.id.buttonCancel) {
                dismiss();
                return;
            }
            if(view.getId()==R.id.buttonBook)
            {   arguments=getArguments();
                Booking booking=createBooking(); //создание заказа
                //создание письма
                javaMailAPI=new JavaMailAPI(getContext(), mailEditText.getText().toString(), Constants.THEME_OF_EMAIL, getActivity().getResources().getString(R.string.text_of_letter, arguments.getString(ARG_NAME),arguments.getString(ARG_DATE),arguments.getString(ARG_TIME),arguments.getString(ARG_PLACE), booking.getCountOfPlaces()));
                //формирование новой записи в БД о бронировании и отправка письма
                bookingFormation(arguments.getString(ARG_DATE),arguments.getString(ARG_TIME), booking);
                dismiss();
                return;
            }
        }
    };

//кастомизация визуального компонента
    public void customizeNumberPicker(int count){
        numberPicker.setMaxValue(count);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
    }

    //метод создания экземпляра класса брониования
    public Booking createBooking()
    {
        name=nameEditText.getText().toString();
        surname=surnameEditText.getText().toString();
        patronomyc=patronomycEditText.getText().toString();
        number=numberEditText.getText().toString();
        count=numberPicker.getValue();
        email=mailEditText.getText().toString();
        Booking booking=new Booking(name, surname, patronomyc, number, email, count);
        return booking;
    }


//метод добавления бронирования
    public void bookingFormation(String dateOfEvent, String timeOfEvent, Booking booking){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_EVENTS).document(dateOfEvent).collection(dateOfEvent).document(timeOfEvent).collection(Constants.BOOKING).
                add(booking).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        javaMailAPI.execute();

                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO: ошибка

                    }
                });

    }



    //метод обновления ифнормации в БД о мероприятии после бронирования
    public void updateInformation(String newValue){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_EVENTS).document(arguments.getString(ARG_DATE)).collection(arguments.getString(ARG_DATE)).document(arguments.getString(ARG_TIME))
                .update(Constants.FIELD_COUNT_OF_PLACES, newValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });

    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_registration_dialog, container, false);
//    }
}
