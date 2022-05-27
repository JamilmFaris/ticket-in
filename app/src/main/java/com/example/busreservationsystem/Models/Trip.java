package com.example.busreservationsystem.Models;


public class Trip {
    public int id;
    public String companyName, sourceName, destinationName;
    public int price;
    public boolean was_booked;
    private String startAt;
    public int year, month //month is subtracted by one for the calendar
            , day, hour, min, sec;

    public Trip(int id, String companyName, String sourceName
            , String destinationName, String startAt, int price, boolean was_booked) {
        this.id = id;
        this.companyName = companyName;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.startAt = startAt;
        this.price = price;
        this.was_booked = was_booked;



        this.year = Integer.parseInt(startAt.substring(0, 4));
        this.month = Integer.parseInt(startAt.substring(5, 7)) - 1;
        this.day = Integer.parseInt(startAt.substring(8, 10));
        this.hour = Integer.parseInt(startAt.substring(11, 13));
        this.min = Integer.parseInt(startAt.substring(14, 16));
        this.sec = Integer.parseInt(startAt.substring(17, 19));
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;

        this.year = Integer.parseInt(startAt.substring(0, 4));
        this.month = Integer.parseInt(startAt.substring(5, 7)) - 1;
        this.day = Integer.parseInt(startAt.substring(8, 10));
        this.hour = Integer.parseInt(startAt.substring(11, 13));
        this.min = Integer.parseInt(startAt.substring(14, 16));
        this.sec = Integer.parseInt(startAt.substring(17, 19));
    }
}
