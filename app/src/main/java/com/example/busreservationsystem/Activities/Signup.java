package com.example.busreservationsystem.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.Models.Passenger;
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    String TAG = "signup";
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    EditText password;
    EditText passwordConfiramtion;
    Button signup;
    RequestQueue queue;
    String url;
    Passenger passenger;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        queue = Volley.newRequestQueue(this);
        url = "http://192.168.1.102/ticket-in-backend/public/api/v1/passengers/signup";
        intent = new Intent(this, Trips.class);

        firstName = findViewById(R.id.passenger_firstname);
        lastName = findViewById(R.id.passenger_lastname);
        phoneNumber = findViewById(R.id.passenger_phonenumber);
        password = findViewById(R.id.passenger_password);
        signup = findViewById(R.id.signup);
        passwordConfiramtion = findViewById(R.id.passenger_password_confirmation);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                passwordConfiramtion.getText().toString().isEmpty() ){
                    Toast.makeText(getApplicationContext(), "Enter missing data", Toast.LENGTH_SHORT).show();
                }/*
                else if(password.getText().toString().length() < 8){
                    Toast.makeText(getApplicationContext(), "password is too short", Toast.LENGTH_SHORT).show();
                }
                */
                else if(phoneNumber.getText().toString().length() != 10){
                    Toast.makeText(getApplicationContext()
                            , "phone number must be 10 digits"
                            , Toast.LENGTH_SHORT).show();
                }
                else if(!password.getText().toString().equals(passwordConfiramtion.getText().toString())){
                    Toast.makeText(getApplicationContext()
                            , "password doesn't match with password confirmation"
                            , Toast.LENGTH_SHORT).show();
                }
                else{
                    postPassenger();
                }
            }
        });

    }


    public void postPassenger(){
        JSONObject params = new JSONObject();
        try {
            params.put("first_name", firstName.getText().toString());
            params.put("last_name", lastName.getText().toString());
            params.put("phone_number", phoneNumber.getText().toString());
            params.put("password", password.getText().toString());
            params.put("password_confirmation", passwordConfiramtion.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST
                , url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Registered"
                                , Toast.LENGTH_SHORT).show();
                        String curToken = null;

                        try {
                            JSONObject passengerObject = response.getJSONObject("passenger");
                            passenger = new Passenger(
                                    passengerObject.getInt("id"),
                                    passengerObject.getString("first_name"),
                                    passengerObject.getString("last_name"),
                                    passengerObject.getString("phone_number")
                            );
                            curToken = response.getString("token");
                            intent.putExtra("passenger", passenger);
                            intent.putExtra("token", curToken);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: json" + e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof AuthFailureError){
                            //token is not valid
                            MainActivity.token = "";
                            startActivity(new Intent(Signup.this, MainActivity.class));
                        }
                        Toast.makeText(Signup.this
                                ,error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}