package com.example.lwb.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.events.calendar.views.EventsCalendar;
import com.example.lwb.Constants;
import com.example.lwb.DateTreatmentMethods;
import com.example.lwb.Models.Event;
import com.example.lwb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment implements EventsCalendar.Callback {
    EventsCalendar eventsCalendar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CalendarFragment.CalendarFragmentInterface calendarFragmentInterface;


    public CalendarFragment() {
        //Запрашиваемый пустой конструктор
    }



    public interface CalendarFragmentInterface {
        void toEventListFragment(String date);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        eventsCalendar = view.findViewById(R.id.calender_event);
        Calendar today = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 2);


        eventsCalendar.setSelectionMode(eventsCalendar.getSINGLE_SELECTION()).
                setToday(today)
                .setMonthRange(today, end)
                .setWeekStartDay(Calendar.MONDAY, true)
                .setIsBoldTextOnSelectionEnabled(false)
                .setDatesTypeface(ResourcesCompat.getFont(getContext(), R.font.gilroy_light))
                .setMonthTitleTypeface(ResourcesCompat.getFont(getContext(), R.font.gilroy_light))
                .setWeekHeaderTypeface(ResourcesCompat.getFont(getContext(), R.font.gilroy_extra_bold)).setCallback(this);

        add_events();
        eventsCalendar.setCurrentSelectedDate(today);
        return view;
    }
    
    @Override
    public void onDayLongPressed(@Nullable Calendar calendar) {
        Calendar selected=eventsCalendar.getCurrentSelectedDate();
        if (eventsCalendar.hasEvent(selected) && (selected.after(Calendar.getInstance()) || DateUtils.isToday(selected.getTimeInMillis()))) {
            String date = new SimpleDateFormat("dd.MM.yyyy").format(selected.getTimeInMillis());
            checkEvents(date);
        }
        else {
            Toast.makeText(getContext(), getText(R.string.alert_inaccessibility_events), Toast.LENGTH_LONG).show();
        }

    }



    @Override
    public void onDaySelected(@Nullable Calendar calendar) {
        Calendar selected=eventsCalendar.getCurrentSelectedDate();
        if (eventsCalendar.hasEvent(selected) && (selected.after(Calendar.getInstance()) || DateUtils.isToday(selected.getTimeInMillis()))) {
            String date = new SimpleDateFormat("dd.MM.yyyy").format(selected.getTimeInMillis());
            checkEvents(date); }
        else {
            Toast.makeText(getContext(), getText(R.string.alert_inaccessibility_events), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMonthChanged(@Nullable Calendar calendar) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CalendarFragment.CalendarFragmentInterface) {
            calendarFragmentInterface = (CalendarFragment.CalendarFragmentInterface) context;
        }

    }

    private void add_events() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_EVENTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                        try {
                            calendar.setTime(sdf.parse(document.getId()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        eventsCalendar.addEvent(calendar);
                        eventsCalendar.build();
                    }
                } else {

                    Toast.makeText(getContext(), getText(R.string.error_unload_events), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

//метод проверки актуальности и наличия мероприятий на выбранную дату
private void checkEvents(String date) {
    db.collection(Constants.COLLECTION_EVENTS).document(date).collection(date).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<Event> eventListOfDay = new ArrayList<>();
                    try {
                        for (DocumentSnapshot document1 : queryDocumentSnapshots.getDocuments()) {
                            Date dateOfEvent = DateTreatmentMethods.getDateFromStrings(document1.getString("time"), document1.getString("date"));
                            if (DateTreatmentMethods.checkRelevanceOfTime(dateOfEvent)) {
                                eventListOfDay.add(new Event(document1.getString("name"), document1.getString("description"), document1.getString("time"), document1.getString("date"), document1.getString("place"), Integer.parseInt(document1.getString("countOfPlaces"))));
                            }
                        }
                        if (eventListOfDay.size() > 0) {
                            String date = new SimpleDateFormat("dd.MM.yyyy").format(eventsCalendar.getCurrentSelectedDate().getTimeInMillis());
                            calendarFragmentInterface.toEventListFragment(date);
                            Log.e("TAG", " NOT EMpTYl ist");
                        } else {
                            Log.e("TAG", "EMpTYl ist");
                            Toast.makeText(getContext(), getText(R.string.alert_inaccessibility_events), Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), getString(R.string.error_unload_events), Toast.LENGTH_LONG).show();
                    }

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), getString(R.string.error_unload_events), Toast.LENGTH_LONG).show();


                }
            });
}
////слушатель при обращении к бд
//private final EventListener<QuerySnapshot> eventListenerCalendar = (value, error) ->{
//    if (error != null){
//        Toast.makeText(getContext(), getString(R.string.error_unload_events), Toast.LENGTH_LONG).show();
//        return;
//    }
//    if (value != null) {
//        List<Event> eventListOfDay = new ArrayList<>();
//        try {
//            for (DocumentSnapshot document1 : value.getDocuments()) {
//                Date dateOfEvent = DateTreatmentMethods.getDateFromStrings(document1.getString("time"), document1.getString("date"));
//                if (DateTreatmentMethods.checkRelevanceOfTime(dateOfEvent)) {
//                    eventListOfDay.add(new Event(document1.getString("name"), document1.getString("description"), document1.getString("time"), document1.getString("date"), document1.getString("place"), Integer.parseInt(document1.getString("countOfPlaces"))));
//                }
//            }
//            if (eventListOfDay.size() > 0) {
//                String date = new SimpleDateFormat("dd.MM.yyyy").format(eventsCalendar.getCurrentSelectedDate().getTimeInMillis());
//                calendarFragmentInterface.toEventListFragment(date);
//                Log.e("TAG", " NOT EMpTYl ist");
//            } else {
//                Log.e("TAG", "EMpTYl ist");
//                Toast.makeText(getContext(), getText(R.string.alert_inaccessibility_events), Toast.LENGTH_LONG).show();
//
//            }
//        } catch (Exception e) {
//            Toast.makeText(getContext(), getString(R.string.error_unload_events), Toast.LENGTH_LONG).show();
//        }
//
//    }
//    else{
//        Toast.makeText(getContext(), getString(R.string.error_unload_events), Toast.LENGTH_LONG).show();
//    }
//};


}