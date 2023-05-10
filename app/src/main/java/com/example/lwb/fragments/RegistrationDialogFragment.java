package com.example.lwb.fragments;


import android.app.AlertDialog;
import android.content.Context;
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
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    NumberPicker numberPicker;
    Bundle arguments;
    String name;
    String surname;
    String patronomyc;
    String number;
    String email;
    int count;
    JavaMailAPI javaMailAPI;
    RegistrationDialogInterface registrationDialogInterface;

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
    public interface RegistrationDialogInterface{
        void updateEventList(String date);

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
        checkAvailability(arguments.getInt(ARG_COUNT));
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
                updateInformationDb(String.valueOf(calculationCountOfPlace(arguments.getInt(ARG_COUNT), Integer.valueOf(booking.getCountOfPlaces()))));
                registrationDialogInterface.updateEventList(arguments.getString(ARG_DATE));

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
    private Booking createBooking()
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
    //рассчет количества мест
    private int calculationCountOfPlace(int countAmount, int numberOfBooked){
        int total=countAmount-numberOfBooked;
        return total;
    }


//метод добавления бронирования
    private void bookingFormation(String dateOfEvent, String timeOfEvent, Booking booking){

        db.collection(Constants.COLLECTION_EVENTS).document(dateOfEvent).collection(dateOfEvent).document(timeOfEvent).collection(Constants.BOOKING).
                add(booking).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        try {
                            javaMailAPI.execute();
                            Log.e("RDF", "BOOK");
                             }
                        catch (Exception e){

                            AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle(getString(R.string.notification));
                            alertDialog.setMessage(e.toString());
                            alertDialog.show();
                            dismiss();
                        }

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
    public void updateInformationDb(String newValue){

        db.collection(Constants.COLLECTION_EVENTS).document(arguments.getString(ARG_DATE)).collection(arguments.getString(ARG_DATE)).document(arguments.getString(ARG_TIME))
                .update(Constants.FIELD_COUNT_OF_PLACES, newValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("REGFRAG", "SUCCESS");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("REGFRAG", "NOT SUCCESS");


                    }
                });

    }
    // проверка  наличия мест
    private void checkAvailability(int count){
        if(count==0){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getString(R.string.notification));
            alertDialog.setMessage(getString(R.string.warning_available));
            alertDialog.show();
            dismiss();
        }
        else {
            return;
        }



    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RegistrationDialogFragment.RegistrationDialogInterface) {
            registrationDialogInterface = (RegistrationDialogFragment.RegistrationDialogInterface) context;
        }
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
