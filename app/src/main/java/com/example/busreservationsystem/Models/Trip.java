package com.example.busreservationsystem.Models;


public class Trip {
    public int id;
    public String companyName, source, destination, startAt;
    public int price;
    public boolean was_booked = false;

    public Trip(int id, String companyName, String source, String destination, String startAt, int price, boolean was_booked) {
        this.id = id;
        this.companyName = companyName;
        this.source = source;
        this.destination = destination;
        this.startAt = startAt;
        this.price = price;
        this.was_booked = was_booked;
    }

    public Trip(String companyName) {
        this.companyName = companyName;
        this.id = 1;
        this.source = "jamdfli";
        this.startAt = "5:00";
        this.price = 7665;
    }
}
