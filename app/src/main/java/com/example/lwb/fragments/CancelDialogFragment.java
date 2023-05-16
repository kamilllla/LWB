package com.example.lwb.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lwb.Constants;
import com.example.lwb.Models.Booking;
import com.example.lwb.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class CancelDialogFragment extends DialogFragment {
    Button cancel;
    Button confirmation;

    FirebaseFirestore db=FirebaseFirestore.getInstance();

    Bundle arguments;
    String bookId;
    String dateOfEvent;
    String timeOfEvent;
    int numberOfReservedPlaces;
    int numberAvailablePlaces;
    String user;
    CancelDialogFragment.CancelDialogInterface cancelDialogInterface;








    public CancelDialogFragment() {

    }

    //пвсевдоконструктор для передачи информации о выбранном событии
    public static CancelDialogFragment newInstance(Booking booking) {
        CancelDialogFragment fragment=new CancelDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constants.BOOKING_ID, booking.getBookId());
        bundle.putString(Constants.BOOKING_EVENT_DATE, booking.getDateOfEvent());
        bundle.putString(Constants.BOOKING_EVENT_TIME, booking.getTimeOfEvent());
        bundle.putInt(Constants.BOOKING_PLACES, booking.getCountOfPlaces());
        fragment.setArguments(bundle);
        return fragment;
    }

    public interface CancelDialogInterface{
        void updateBookingList();
    }




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_cancel_dialog, container, false);
        arguments=getArguments();
        cancel = view.findViewById(R.id.buttonCancel);
        confirmation = view.findViewById(R.id.buttonDelete);
        initializationOfBookingData();
        confirmation.setOnClickListener(buttonClick);
        cancel.setOnClickListener(buttonClick);
        return  view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CancelDialogFragment.CancelDialogInterface) {
            cancelDialogInterface= (CancelDialogFragment.CancelDialogInterface) context;
        }
    }


    View.OnClickListener buttonClick =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId()==R.id.buttonCancel) {
                dismiss();
                return;
            }
            if(view.getId()==R.id.buttonDelete)
            {
                deletingBook(dateOfEvent,timeOfEvent, bookId, user);
                Log.e("TOTAL_PLACES", String.valueOf(numberAvailablePlaces));
                updateInformationAboutEvent(String.valueOf(calculationCountOfPlace(numberAvailablePlaces,numberOfReservedPlaces)));
                cancelDialogInterface.updateBookingList();
                Log.e("delete","cancelDialogInterface.updateBookingList();");
                dismiss();
                return;
            }
        }
    };


    //инифиализация глобальных переменных для их дальнейшего использования
    private void initializationOfBookingData(){
        arguments=getArguments();
        dateOfEvent=arguments.getString(Constants.BOOKING_EVENT_DATE);
        timeOfEvent=arguments.getString(Constants.BOOKING_EVENT_TIME);
        bookId=arguments.getString(Constants.BOOKING_ID);
        numberOfReservedPlaces=arguments.getInt(Constants.BOOKING_PLACES);
        user=getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.USER_NAME, Constants.USER_NAME);
        db.collection(Constants.COLLECTION_EVENTS).
                document(dateOfEvent).collection(dateOfEvent).document(timeOfEvent)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        numberAvailablePlaces=Integer.valueOf(documentSnapshot.getString(Constants.FIELD_COUNT_OF_PLACES));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    };




    //рассчет количества мест
    private int calculationCountOfPlace(int countAmount, int numberOfBooked){
        int total=countAmount+numberOfBooked;
        return total;
    }


    //метод удаления бронирования
    private void deletingBook(String dateOfEvent, String timeOfEvent,String bookingId, String user){
        db.collection(Constants.COLLECTION_EVENTS).
                document(dateOfEvent).collection(dateOfEvent).document(timeOfEvent).
                collection(Constants.BOOKING)
                .document(bookingId).delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deletingBookAccount(user,bookId);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.notification));
                alertDialog.setMessage(e.toString());
                alertDialog.show();
                dismiss();

            }
        });


    }
    //метод добавления ифнормации о бронировании пользователю
    private void deletingBookAccount(String accountId, String bookId){
        db.collection(Constants.COLLECTION_USERS).document(accountId)
        .collection(Constants.BOOKING).document(bookId).
        delete()
                 .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle(getString(R.string.notification));
                        alertDialog.setMessage(e.toString());
                        alertDialog.show();
                        dismiss();

                    }
                });


    }



    //метод обновления ифнормации в БД о мероприятии после бронирования
    public void updateInformationAboutEvent(String newValue){

        db.collection(Constants.COLLECTION_EVENTS).document(arguments.getString(Constants.BOOKING_EVENT_DATE))
                .collection(arguments.getString(Constants.BOOKING_EVENT_DATE)).document(arguments.getString(Constants.BOOKING_EVENT_TIME))
                .update(Constants.FIELD_COUNT_OF_PLACES, newValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("UPDATE_PLACES", "SUCCESS");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UPDATE_Places", "NOT SUCCESS");
                        Toast.makeText(getContext(),getText(R.string.error_cancel_book), Toast.LENGTH_LONG).show();

                    }
                });

    }



}
