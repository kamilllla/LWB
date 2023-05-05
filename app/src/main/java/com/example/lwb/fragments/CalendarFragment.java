package com.example.lwb.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.events.calendar.views.EventsCalendar;
import com.example.lwb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class CalendarFragment extends Fragment implements EventsCalendar.Callback {
    EventsCalendar eventsCalendar;
    CalendarFragment.CalendarFragmentInterface calendarFragmentInterface;


    public CalendarFragment() {
        //Запрашиваемый пустой конструктор
    }

//псевдокоснтруктор для передачи
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface CalendarFragmentInterface {
        public void toEventListFragment(String date);

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
        return view;
    }

    @Override
    public void onDayLongPressed(@Nullable Calendar calendar) {

    }

    @Override
    public void onDaySelected(@Nullable Calendar calendar) {
        eventsCalendar.getCurrentSelectedDate();
        if (eventsCalendar.hasEvent(eventsCalendar.getCurrentSelectedDate())) {
            String date = new SimpleDateFormat("dd.MM.yyyy").format(eventsCalendar.getCurrentSelectedDate().getTimeInMillis());
            calendarFragmentInterface.toEventListFragment(date);

            Log.e("DAY", "true - " + date);

        } else
            Log.e("DAY", "false");

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

    public void add_events() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //document.getId().toString() document.get("по").toString();
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                        try {
                            calendar.setTime(sdf.parse(document.getId()));// all done
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        eventsCalendar.addEvent(calendar);
                        eventsCalendar.build();
                        Log.e("TAG", "Added event");

                    }
                } else {
                    Log.e("TAG", "ERROR ERROR ERrOOOR WE HAVE PROBLEMS");
                }
            }
        });
    }
}