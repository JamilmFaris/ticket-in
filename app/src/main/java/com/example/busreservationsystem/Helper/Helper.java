package com.example.busreservationsystem.Helper;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.example.busreservationsystem.Activities.Trips;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class Helper {


    public static String onAuthFailureError(VolleyError error){// returns the response string
                                                        // if it is an AuthFailureError and empty otherwise
        if(error instanceof AuthFailureError){// functionality when AuthFailureError
            //token is not valid
            MainActivity.token = "";
            String responseMessage = "you have to login again";
            return responseMessage;
        }
        return "";
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


}
