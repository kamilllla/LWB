package com.example.lwb.adapters;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lwb.Categories;
import com.example.lwb.Guid;
import com.example.lwb.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SecondAdapter extends RecyclerView.Adapter<SecondAdapter.ViewHolder>  {
    private List<Guid> guids;
    Categories category;
    private Context thisContext;

    public interface OnGuidClickListenerInterface{
        void onClickGuid(Guid guid, Categories category);

    }
    public final OnGuidClickListenerInterface interfaceClick;


    public SecondAdapter(List<Guid> guids,OnGuidClickListenerInterface onClickListener, Categories category) {
        this.interfaceClick = onClickListener;
        this.category=category;
        this.guids = guids;

    }

    //-----------------------------------------------------------------------------------------

    //внутренний класс, который постоянно содержит ссылку на нужные элементы
    public class ViewHolder extends RecyclerView.ViewHolder {
        //View элементы, значения для которых будут установлены для отображения в каждой строке
        public TextView nameTextView;
        public ImageView photoImg;
        public TextView genreTextview;

        //конструктор
        public ViewHolder(View itemView) {
            // сохраняет view, которую можно использовать для доступа к контексту из любого экземпляра ViewHolder .
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.textViewName);
            photoImg = (ImageView) itemView.findViewById(R.id.child_image);

        }
    }
    //----------------------------------------------------------------------------------------

    // создание новой ячейки
    @Override
    public SecondAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //создание из содержимого layout View-элемента
        View contactView = inflater.inflate(R.layout.child_activity, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // приязываем данные к ячейки
    @Override
    public void onBindViewHolder(SecondAdapter.ViewHolder holder, int position) {
        // / получаем объект списка по id
        Guid guid = guids.get(holder.getAdapterPosition());
        // устанавливаем данные View на основе макета(содержашемся в holder)
        holder.nameTextView.setText(guid.getName());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://lwbf-d3376.appspot.com");
        storageRef.child(guid.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri). error(R.drawable.ic_launcher_background)
                        .placeholder(R.drawable.loading).into(holder.photoImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("err", "error");
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceClick.onClickGuid(guid, category);
            }
        });
    }

    // возвращает количество элементов списка
    @Override
    public int getItemCount() {
        return guids.size();
    }

}

