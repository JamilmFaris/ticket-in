package com.example.busreservationsystem.Helper;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.example.busreservationsystem.Activities.Login;
import com.example.busreservationsystem.Activities.Trips;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.Models.Passenger;
import com.example.busreservationsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class Helper {


    public static void onAuthFailureError(Context context, VolleyError error){// returns the response string
                                                        // if it is an AuthFailureError and empty otherwise
        if(error instanceof AuthFailureError){// functionality when AuthFailureError
            //token is not valid
            String responseMessage = "you have to login again";
            removeToken(context);
            Toast.makeText(context,
                    responseMessage
                    , Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, Login.class));
        }
        else{
            String responseMessage = onErrorResponse(error);
            if(!responseMessage.isEmpty()){
                Toast.makeText(context,
                        responseMessage
                        , Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String onErrorResponse(VolleyError error){ // returns the response
        String message = "";
        try {
            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            JSONObject data = new JSONObject(responseBody);
            message = data.optString("message");
        } catch (Exception e) {
            e.printStackTrace();
            message = "an error occurred";
        }
        return message;
    }

    public static boolean isNumber(char x) {
        return (x <= '9' && x >= '0');
    }

    public static String isPhoneNumber(String digits) {
        String returnMessage = "";
        if(digits.length() == 10){
            boolean isDigits = true;
            for(int i = 0;i < digits.length();i++){
                if(!Helper.isNumber(digits.charAt(i))){
                    isDigits = false;
                }
            }
            if(!isDigits){
                returnMessage = "phone number must be only digits";
            }
            else{// is a phone number
                returnMessage = "";
            }
        }
        else {
            returnMessage = "phone number must be 10 digits";
        }
        return returnMessage;
    }

    public static void saveToken(Context context, String token){
        // Creating a shared pref object
        // with a file name "MySharedPref"
        // in private mode
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString("token", token);
        myEdit.apply();
    }

    public static String loadToken(Context context){
        // Fetching the stored data
        // from the SharedPreference
        SharedPreferences sh = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        return sh.getString("token", "");
    }

    public static void removeToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.clear();
        myEdit.apply();
    }


    public static void savePassenger(Context context, Passenger passenger){
        // Creating a shared pref object
        // with a file name "MySharedPref"
        // in private mode
        SharedPreferences sharedPreferences = context.getSharedPreferences("passengerSharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        JSONObject passengerJsonObject = new JSONObject();
        try {
            passengerJsonObject.put("firstName", passenger.getFirstName());
            passengerJsonObject.put("lastName", passenger.getLastName());
            passengerJsonObject.put("phoneNumber", passenger.getPhoneNumber());
            passengerJsonObject.put("id", passenger.getId());
            // write all the data entered by the user in SharedPreference and apply
            myEdit.putString("passenger", passengerJsonObject.toString());
            myEdit.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public static Passenger loadPassenger(Context context){
        // Fetching the stored data
        // from the SharedPreference
        SharedPreferences sh = context.getSharedPreferences("passengerSharedPref", Context.MODE_PRIVATE);
        String firstName = null, lastName = null, phoneNumber = null;
        int id = -1;
        try {
            JSONObject passengerJsonObject = new JSONObject(sh.getString("passenger", ""));
            firstName = passengerJsonObject.getString("firstName");
            lastName = passengerJsonObject.getString("lastName");
            phoneNumber = passengerJsonObject.getString("phoneNumber");
            id = passengerJsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Passenger passenger = new Passenger(id, firstName, lastName, phoneNumber);
        return passenger;
    }

    public static void removePassenger(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("passengerSharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.clear();
        myEdit.apply();
    }
}
