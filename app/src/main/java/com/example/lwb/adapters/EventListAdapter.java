package com.example.lwb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lwb.Event;
import com.example.lwb.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;


import java.util.List;



public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private List<Event> events;
    private Context thisContext;
    private EventListInterface eventListInterface;

   public interface EventListInterface{
        public void toDialogFragment(Event event);
    }





    public EventListAdapter(List<Event> events, Context thisContext, EventListInterface eventListInterface) {
        this.events = events;
        this.thisContext = thisContext;
        this.eventListInterface=eventListInterface;


    }
    //-----------------------------------------------------------------------------------------

    //внутренний класс, который постоянно содержит ссылку на нужные элементы
    public class ViewHolder extends RecyclerView.ViewHolder {
        //View элементы, значения для которых будут установлены для отображения в каждой строке
        public TextView name;
        public TextView time;
        public Button buttonAdd;
        public ExpandableTextView descriptionExpandableTextView;


        //конструктор
        public ViewHolder(View itemView) {
            // сохраняет view, которую можно использовать для доступа к контексту из любого экземпляра ViewHolder .
            super(itemView);
            name =  itemView.findViewById(R.id.nameTextView);
            time =  itemView.findViewById(R.id.timeTextView);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);

            descriptionExpandableTextView = itemView.findViewById(R.id.expand_text_view);

        }

    }
    //----------------------------------------------------------------------------------------


    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_of_events_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(EventListAdapter.ViewHolder holder, int position) {
        // получаем объект списка по id
        Event event = events.get(holder.getAdapterPosition());
        // устанавливаем данные View на основе макета(содержашемся в holder)
        holder.name.setText(event.getName());
        holder.time.setText(event.getTime());
        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventListInterface.toDialogFragment(event);
            }
        });
        holder.descriptionExpandableTextView.setText("Описание..."+event.getDescription());

    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

