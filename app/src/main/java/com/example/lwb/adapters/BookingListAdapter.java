package com.example.lwb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lwb.Models.Booking;
import com.example.lwb.R;


import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.ViewHolder> {

    private List<Booking> books;
    private Context thisContext;
    private BookingListAdapter.BookingListAdapterInterface bookingListAdapterInterface;

    public interface BookingListAdapterInterface{
        public void toDialogFragment(Booking booking);
    }

    public BookingListAdapter(List<Booking> books, Context thisContext, BookingListAdapterInterface bookingListAdapterInterface) {
        this.books = books;
        this.thisContext = thisContext;
        this.bookingListAdapterInterface = bookingListAdapterInterface;
    }


//-----------------------------------------------------------------------------------------

    //внутренний класс, который постоянно содержит ссылку на нужные элементы
    public class ViewHolder extends RecyclerView.ViewHolder {
        //View элементы, значения для которых будут установлены для отображения в каждой строке
        public TextView name;
        public TextView timeAndDate;
        public TextView count;
        public Button buttonDelete;



        //конструктор
        public ViewHolder(View itemView) {
            // сохраняет view, которую можно использовать для доступа к контексту из любого экземпляра ViewHolder .
            super(itemView);
            name =  itemView.findViewById(R.id.nameTextView);
            timeAndDate =  itemView.findViewById(R.id.timeAnDateTextView);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            count=itemView.findViewById(R.id.countBookingPlaces);

        }

    }
    //----------------------------------------------------------------------------------------


    @Override
    public BookingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View bookingListView = inflater.inflate(R.layout.item_of_personal_booking, parent, false);
        BookingListAdapter.ViewHolder viewHolder = new BookingListAdapter.ViewHolder(bookingListView);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(BookingListAdapter.ViewHolder holder, int position) {
        // получаем объект списка по id
        Booking booking = books.get(holder.getAdapterPosition());
        // устанавливаем данные View на основе макета(содержашемся в holder)
        holder.name.setText(booking.getNameOfEvent());
        holder.timeAndDate.setText(booking.getTimeOfEvent()+" "+booking.getDateOfEvent());
        holder.count.setText(String.valueOf(booking.getCountOfPlaces()));
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingListAdapterInterface.toDialogFragment(booking);
            }
        });


    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}

