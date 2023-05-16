package com.example.lwb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lwb.Models.Categories;
import com.example.lwb.R;

import java.util.List;

public class GuidAdapter extends RecyclerView.Adapter<GuidAdapter.ViewHolder> {
        private List<Categories> categories;
        private Context thisContext;
        SecondAdapter.OnGuidClickListenerInterface onGuidClickListenerInterface;



        public GuidAdapter(List<Categories> categories, Context context, SecondAdapter.OnGuidClickListenerInterface onGuidClickListenerInterface1) {
            onGuidClickListenerInterface=onGuidClickListenerInterface1;
            this.categories = categories;
            this.thisContext = context;

        }
        public void setFilteredList(List<Categories> filteredList){
            this.categories=filteredList;
            notifyDataSetChanged();
        }



        //-----------------------------------------------------------------------------------------

        //внутренний класс, который постоянно содержит ссылку на нужные элементы
        public class ViewHolder extends RecyclerView.ViewHolder {
            //View элементы, значения для которых будут установлены для отображения в каждой строке
            public TextView headerText;
            public RecyclerView recyclerView;
            DividerItemDecoration dividerItemDecoration;


            //конструктор
            public ViewHolder(View itemView) {
                // сохраняет view, которую можно использовать для доступа к контексту из любого экземпляра ViewHolder .
                super(itemView);
                headerText= (TextView) itemView.findViewById(R.id.header_text);
                recyclerView= (RecyclerView) itemView.findViewById(R.id.recyclerView2);
            }
        }
        //----------------------------------------------------------------------------------------

        // создание новой ячейки
        @Override
        public GuidAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            //создание из содержимого layout View-элемента
            View contactView = inflater.inflate(R.layout.header_layout, parent, false);
            GuidAdapter.ViewHolder viewHolder = new GuidAdapter.ViewHolder(contactView);
            return viewHolder;
        }



        // приязываем данные к ячейки
        @Override
        public void onBindViewHolder(GuidAdapter.ViewHolder holder, int position) {
            // / получаем объект списка по id
            Categories category = categories.get(position);
            SecondAdapter secondAdapter= new SecondAdapter(category.getGuids(), onGuidClickListenerInterface, category);
            // устанавливаем данные View на основе макета(содержашемся в holder)
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(thisContext,RecyclerView.HORIZONTAL,false);
            holder.headerText.setText( category.getNameOfCategorie());
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.setAdapter(secondAdapter);

        }
        // возрашает количсетво элементов списка
        @Override
        public int getItemCount() {
            return categories.size();
        }


}
