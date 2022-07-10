package com.example.busreservationsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.busreservationsystem.Activities.Login;
import com.example.busreservationsystem.Activities.Signup;
import com.example.busreservationsystem.Activities.Trips;
import com.example.busreservationsystem.Adapter.TripsAdapter;
import com.example.busreservationsystem.ClickListener.ClickListener;
import com.example.busreservationsystem.Helper.Helper;
import com.example.busreservationsystem.Models.Trip;
import com.example.busreservationsystem.Notification.Notification;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    //todo save token
    public static String SHARED_PREFS = "sharedPrefs";
    String url;
    public static Button login, signup;


    public String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = Helper.loadToken(this);
        /// if we have token then go to the Trips activity
        if(!token.isEmpty()){
            Intent intent = new Intent(this, Trips.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }


        ///initialize
        login = findViewById(R.id.go_login);
        signup = findViewById(R.id.go_signup);

        //

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goLogin = new Intent(MainActivity.this, Login.class);
                startActivity(goLogin);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goSignup = new Intent(MainActivity.this, Signup.class);
                startActivity(goSignup);
            }
        });


    }
}