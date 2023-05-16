package com.example.lwb.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lwb.Constants;
import com.example.lwb.DateTreatmentMethods;
import com.example.lwb.Models.Booking;
import com.example.lwb.R;
import com.example.lwb.adapters.BookingListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BooksListFragment extends Fragment {

    FirebaseFirestore db=FirebaseFirestore.getInstance();

    List<Booking> books=new ArrayList<>();
    BooksListFragment.BooksListFragmentInterface booksListFragmentInterface;
    String user;
    RecyclerView recyclerView;
    TextView textViewAlert;
    Button buttonClose;




    //требуемый конструктор
    public BooksListFragment() {

    }


    public interface BooksListFragmentInterface{
        void showDialogFragmentForDeleting(Booking book);
        void showAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_books_list, container, false);
        recyclerView=view.findViewById(R.id.recycleView);
        textViewAlert=view.findViewById(R.id.textAlert);
        buttonClose=view.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(clickButton);

        user=getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
             .getString(Constants.USER_NAME, Constants.USER_NAME);
        checkBooks(user);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BooksListFragment.BooksListFragmentInterface) {
            booksListFragmentInterface = (BooksListFragment.BooksListFragmentInterface) context;
        }

    }
    //интерфейс адаптера
    BookingListAdapter.BookingListAdapterInterface bookingListAdapterInterface=new BookingListAdapter.BookingListAdapterInterface() {
        @Override
        public void toDialogFragment(Booking booking) {
            booksListFragmentInterface.showDialogFragmentForDeleting(booking);

        }
    };
    //метод проверки наличия бронированных мест
    private void checkBooks(String user) {
        List<String> bookingPersonalList=new ArrayList<>();
        db.collection(Constants.COLLECTION_USERS).document(user).collection(Constants.BOOKING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document1 : queryDocumentSnapshots.getDocuments()){
                            //получения даты и времения конкретного брогиварония
                            String dateOfTheBookedEvent=document1.getString(Constants.BOOKING_EVENT_DATE);
                            String timeOfTheBookedEvent=document1.getString(Constants.BOOKING_EVENT_TIME);
                            //преобразование полученных данных
                            Date dateAndTimeOfEvent= DateTreatmentMethods.getDateFromStrings(timeOfTheBookedEvent,dateOfTheBookedEvent);
                            //проверка актуальности даты и времени забронированного события
                            if (DateTreatmentMethods.checkRelevanceOfTime(dateAndTimeOfEvent)) {
                                bookingPersonalList.add(document1.getId());
                            }
                        }
                        //проверка, есть ли доступные бронирования
                        if (bookingPersonalList.size()>0) {

                            loadBooks();
                            textViewAlert.setVisibility(View.GONE);
                        }
                        else {
                            textViewAlert.setText(getText(R.string.alert_inaccessibility_books));
                            textViewAlert.setVisibility(View.VISIBLE);
                        }
                    }
                })
                //слушатель при событии невозомжного обращения к бд
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getText(R.string.alert_inaccessibility_books), Toast.LENGTH_LONG).show();
                        textViewAlert.setVisibility(View.VISIBLE);

                    }
                });
    }

    //выгрузка всех броней
    public void loadBooks(){
        db.collection(Constants.COLLECTION_USERS).document(user).collection(Constants.BOOKING)
                .get().addOnCompleteListener(

                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {

                                for (QueryDocumentSnapshot document1 : task.getResult()) {
                                    Date dateOfEvent= DateTreatmentMethods.getDateFromStrings(document1.getString(Constants.BOOKING_EVENT_TIME),document1.getString(Constants.BOOKING_EVENT_DATE));
                                    if (DateTreatmentMethods.checkRelevanceOfTime(dateOfEvent)) {
                                        books.add(new Booking(document1.getString(Constants.BOOKING_ID),
                                                            document1.getString(Constants.BOOKING_NAME),
                                                            document1.getString(Constants.BOOKING_SURNAME),
                                                            document1.getString(Constants.BOOKING_PATRONUMIC),
                                                            document1.getString(Constants.BOOKING_NUMBER),
                                                            document1.getString(Constants.BOOKING_EMAIL),
                                                            document1.getString(Constants.BOOKING_ACCOUNT),
                                                            document1.getString(Constants.BOOKING_EVENT_NAME),
                                                            document1.getString(Constants.BOOKING_EVENT_TIME),
                                                            document1.getString(Constants.BOOKING_EVENT_DATE),
                                               Integer.valueOf(document1.get(Constants.BOOKING_PLACES).toString())));

                                        BookingListAdapter adapter = new BookingListAdapter(books, getContext(), bookingListAdapterInterface);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    }
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(getContext(),getString(R.string.error_unload_events),Toast.LENGTH_LONG ).show();

                            }
                        }
                        else {
                            Toast.makeText(getContext(),getString(R.string.alert_inaccessibility_books),Toast.LENGTH_LONG ).show();
                        }

                    }
                }
        );
    }
    //слушатель события нажатия на кнопку
     private View.OnClickListener clickButton=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            booksListFragmentInterface.showAccountFragment();

        }
    };


}