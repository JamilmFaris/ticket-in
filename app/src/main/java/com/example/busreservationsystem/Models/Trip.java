package com.example.busreservationsystem.Models;


public class Trip {
    public int id;
    public String companyName, sourceName, destinationName;
    public int price;
    public boolean was_booked;
    private String startAt;

    public Trip(int id, String companyName, String sourceName
            , String destinationName, int price, boolean was_booked) {
        this.id = id;
        this.companyName = companyName;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.price = price;
        this.was_booked = was_booked;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }
}
