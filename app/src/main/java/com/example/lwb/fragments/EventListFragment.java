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
import android.widget.Toast;

import com.example.lwb.Constants;
import com.example.lwb.DateTreatmentMethods;
import com.example.lwb.Models.Event;
import com.example.lwb.R;
import com.example.lwb.adapters.EventListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EventListFragment extends Fragment {
    String date;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    Bundle bundle;
    List<Event> events=new ArrayList<>();
    EventListFragmentInterface eventListFragmentInterface;





    public EventListFragment() {
        // Required empty public constructor
    }


    public static EventListFragment newInstance(String date) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        return fragment;
    }

    public interface EventListFragmentInterface{
        void showDialogFragment(Event event);
        void showCalendarFragment();
    }

    EventListAdapter.EventListInterface eventListInterface= new EventListAdapter.EventListInterface() {
        @Override
        public void toDialogFragment(Event event) {
            eventListFragmentInterface.showDialogFragment(event);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_event_list, container, false);
        RecyclerView recyclerView=view.findViewById(R.id.recycleView);
        Button buttonClose=view.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(clickButton);

        bundle=getArguments();
        date=bundle.getString("date");
        db.collection(Constants.COLLECTION_EVENTS).document(date).collection(date).get().addOnCompleteListener(

                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {

                                for (QueryDocumentSnapshot document1 : task.getResult()) {

                                      Date dateOfEvent= DateTreatmentMethods.getDateFromStrings(document1.getString("time"),document1.getString("date"));
                                      if (DateTreatmentMethods.checkRelevanceOfTime(dateOfEvent)) {
                                          events.add(new Event(document1.getString("name"), document1.getString("description"), document1.getString("time"), document1.getString("date"), document1.getString("place"), Integer.parseInt(document1.getString("countOfPlaces"))));
                                          EventListAdapter adapter = new EventListAdapter(events, getContext(), eventListInterface);
                                          recyclerView.setAdapter(adapter);
                                          recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                      }
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(getContext(),getString(R.string.error_unload_events),Toast.LENGTH_LONG ).show();

                            }
                        }

                    }
                }
        );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventListFragment.EventListFragmentInterface) {
            eventListFragmentInterface = (EventListFragment.EventListFragmentInterface) context;
        }

    }
    //слушатель события нажатия на кнопку
    private View.OnClickListener clickButton=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            eventListFragmentInterface.showCalendarFragment();

        }
    };


}