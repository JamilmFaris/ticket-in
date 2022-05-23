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
    TextView was_booked;
    public TripsViewHolder(@NonNull View itemView) {
        super(itemView);
        companyName = itemView.findViewById(R.id.trip_company_name);
        fromTo = itemView.findViewById(R.id.from_to);
        startAt = itemView.findViewById(R.id.start_at);
        price = itemView.findViewById(R.id.price);
        was_booked = itemView.findViewById(R.id.was_booked);
    }
    public void bind(Trip trip){
        companyName.setText("company : " + trip.companyName);
        fromTo.setText("from " + trip.sourceName + " to " + trip.destinationName );
        startAt.setText("start at : " + trip.getStartAt());
        price.setText("price : " + trip.price);
        if(trip.was_booked){
            was_booked.setVisibility(View.VISIBLE);
        }
        else{
            was_booked.setVisibility(View.GONE);
        }
    }
}
