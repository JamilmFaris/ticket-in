package com.example.busreservationsystem.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busreservationsystem.ClickListener.ClickListener;
import com.example.busreservationsystem.Models.Trip;
import com.example.busreservationsystem.R;
import com.example.busreservationsystem.ViewHolder.TripsViewHolder;

import java.util.ArrayList;

public class TripsAdapter extends RecyclerView.Adapter<TripsViewHolder> {
    ArrayList<Trip> trips = new ArrayList<>();
    ClickListener clickListener;
    public TripsAdapter(ClickListener clickListener){
        this.clickListener = clickListener;
    }
    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_holder, null);
        return new TripsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        holder.bind(trips.get(position));
        int index = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.click(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }
    public void addItems(ArrayList<Trip> trips){
        this.trips.clear();
        this.trips.addAll(trips);
        notifyDataSetChanged();
    }
}
