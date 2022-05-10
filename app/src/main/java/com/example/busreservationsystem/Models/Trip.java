package com.example.busreservationsystem.Models;


public class Trip {
    public int id;
    public String companyName, fromTo, startAt;
    public int price;

    public Trip(int id, String companyName, String fromTo, String startAt, int price) {
        this.id = id;
        this.companyName = companyName;
        this.fromTo = fromTo;
        this.startAt = startAt;
        this.price = price;
    }

    public Trip(String companyName) {
        this.companyName = companyName;
        this.id = 1;
        this.fromTo = "jamdfli";
        this.startAt = "5:00";
        this.price = 7665;
    }
}
