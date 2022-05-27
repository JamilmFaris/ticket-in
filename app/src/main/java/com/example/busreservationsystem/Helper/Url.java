package com.example.busreservationsystem.Helper;

import com.example.busreservationsystem.MainActivity;

public class Url {
    private static String prefix = "http://192.168.1.104/ticket-in-backend/public";
    public static String getSignupUrl(){
        String url = prefix + "/api/v1/passengers/signup";
        return url;
    }

    public static String getLoginUrl(){
        String url = prefix + "/api/v1/passengers/login";
        return url;
    }

    public static String getBookTripUrl(int tripId){
        String url = prefix + "/api/v1/trips";
        url += "/" + tripId;
        url += "/book";
        return url;
    }

    public static String getBookedTripUrl(int passengerId){
        String url = prefix + "/api/v1/passengers";
        url += "/" + passengerId;
        url += "/trips/upcoming";
        return url;
    }

    public static String getCancelTripUrl(int tripId){
        String url = prefix + "/api/v1/trips";
        url += "/" + tripId;
        url += "/cancelBook";
        return url;
    }

    public static String getGetTripsUrl(String companyHandle, String sourceSlug, String destinationSlug){
        String url = prefix + "/api/v1/trips";
        url += "?company=" + companyHandle;
        url += "&source=" + sourceSlug;
        url += "&destination=" + destinationSlug;
        return url;
    }

    public static String getGetCompaniesHandleUrl(){
        String url = prefix + "/api/v1/companies";
        return url;
    }


    public static String getGetCitiesSlugUrl(){
        String url = prefix + "/api/v1/cities";
        return url;
    }

    public static String getEditProfileUrl(){
        String url = prefix + "";
        return url;
    }
}
