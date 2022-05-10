package com.example.busreservationsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.busreservationsystem.Models.Trip;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static String SHARED_PREFS = "sharedPrefs";

    String url;

    public static Button login, signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        startActivity(new Intent(MainActivity.this, Trips.class));

    }

}