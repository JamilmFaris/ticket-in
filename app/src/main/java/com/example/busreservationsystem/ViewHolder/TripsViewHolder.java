package com.example.busreservationsystem.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busreservationsystem.Models.Trip;
import com.example.busreservationsystem.R;

public class TripsViewHolder extends RecyclerView.ViewHolder {
    TextView companyName;
    TextView fromTo;
    TextView startAt;
    TextView price;
    public TripsViewHolder(@NonNull View itemView) {
        super(itemView);
        companyName = itemView.findViewById(R.id.trip_company_name);
        fromTo = itemView.findViewById(R.id.from_to);
        startAt = itemView.findViewById(R.id.start_at);
        price = itemView.findViewById(R.id.price);
    }
    public void bind(Trip trip){
        companyName.setText(trip.companyName);
        fromTo.setText(trip.fromTo);
        startAt.setText(trip.startAt);
        price.setText(String.valueOf(trip.price));
    }
}
