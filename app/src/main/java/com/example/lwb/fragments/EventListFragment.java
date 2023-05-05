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

import com.example.lwb.Event;
import com.example.lwb.R;
import com.example.lwb.adapters.EventListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;


public class EventListFragment extends Fragment {
    String date;
    FirebaseFirestore db;
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
        public void showDialogFragment(Event event);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_event_list, container, false);
        RecyclerView recyclerView=view.findViewById(R.id.recycleView);
        db=FirebaseFirestore.getInstance();
        bundle=getArguments();
        date=bundle.getString("date");
        db.collection("events").document(date).collection(date).get().addOnCompleteListener(

                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                events.add(new Event(document1.getString("name"), document1.getString("description"), document1.getString("time"), document1.getString("date"), document1.getString("place"), Integer.parseInt(document1.getString("countOfPlaces"))));
                                EventListAdapter adapter=new EventListAdapter(events, getContext(), eventListInterface);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
}