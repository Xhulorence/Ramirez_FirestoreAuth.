package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReflectionAdapter extends RecyclerView.Adapter<ReflectionAdapter.ReflectionViewHolder> {

    private List<Reflection> reflectionList;
    private OnReflectionListener listener;

    public interface OnReflectionListener {
        void onEditClick(Reflection reflection);
        void onDeleteClick(Reflection reflection);
    }

    public ReflectionAdapter(List<Reflection> reflectionList, OnReflectionListener listener) {
        this.reflectionList = reflectionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReflectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reflection, parent, false);
        return new ReflectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReflectionViewHolder holder, int position) {
        Reflection reflection = reflectionList.get(position);
        holder.tvTitle.setText(reflection.getTitle());
        holder.tvVerse.setText(reflection.getBibleVerse());
        holder.tvNotes.setText(reflection.getReflectionNotes());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(reflection);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(reflection);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reflectionList.size();
    }

    public static class ReflectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvVerse, tvNotes;
        ImageButton btnEdit, btnDelete;

        public ReflectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvVerse = itemView.findViewById(R.id.tvItemVerse);
            tvNotes = itemView.findViewById(R.id.tvItemNotes);
            btnEdit = itemView.findViewById(R.id.btnEditReflection);
            btnDelete = itemView.findViewById(R.id.btnDeleteReflection);
        }
    }
}
