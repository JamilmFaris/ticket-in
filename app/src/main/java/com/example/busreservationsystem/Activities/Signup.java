package com.example.busreservationsystem.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    EditText password;
    EditText passwordConfiramtion;
    Button signup;
    RequestQueue queue;
    String url;
    Passenger passenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        queue = Volley.newRequestQueue(this);
        // TODO : change the url
        url = "http://192.168.43.239/ticket-in-backend/public/api/v1/passengers/signup";
        Intent intent = new Intent(this, Trips.class);

        firstName = findViewById(R.id.passenger_firstname);
        lastName = findViewById(R.id.passenger_lastname);
        phoneNumber = findViewById(R.id.passenger_phonenumber);
        password = findViewById(R.id.passenger_password);
        signup = findViewById(R.id.signup);
        passwordConfiramtion = findViewById(R.id.passenger_password_confirmation);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passenger = new Passenger(firstName.getText().toString(), lastName.getText().toString()
                        , phoneNumber.getText().toString());


                if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                passwordConfiramtion.getText().toString().isEmpty() ){
                    Toast.makeText(getApplicationContext(), "Enter some data", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().length() < 8){
                    Toast.makeText(getApplicationContext(), "password is too short", Toast.LENGTH_SHORT).show();
                }
                else if(!password.getText().toString().equals(passwordConfiramtion.getText().toString())){
                    Toast.makeText(getApplicationContext()
                            , "password doesn't match with password confirmation"
                            , Toast.LENGTH_SHORT).show();
                }
                else{
                    postPassenger(passenger);
                    intent.putExtra("passenger", passenger);
                    //todo : remove comment
                    //startActivity(intent);
                }
            }
        });

    }


    public void postPassenger(Passenger passenger){
        /*HashMap<String, String> params = new HashMap<>();
        params.put("first_name", "forbidding");
        params.put("last_name", "sidebands");
        params.put("phone_number", "0932753331");
        params.put("password", "jamiljamil");
        params.put("password_confirmation", "jamiljamil");*/


        JSONObject params = new JSONObject();
        try {
            params.put("first_name", passenger.getFirstName());
            params.put("last_name", passenger.getLastName());
            params.put("phone_number", passenger.getPhoneNumber());
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
                        try {
                            //todo : remove
                            Toast.makeText(getApplicationContext()
                                    , response.getString("token")
                                    , Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String curToken = null;
                        try {
                            curToken = response.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "json " + e.toString()
                                    , Toast.LENGTH_SHORT).show();
                        }
                        passenger.setToken(curToken);
                        MainActivity.token = curToken;
                        Intent intent = new Intent(Signup.this, Trips.class);
                        intent.putExtra("passenger", passenger);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Problem " + error.toString()
                                , Toast.LENGTH_SHORT).show();
                    }
                }){    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        // TODO : try the shared preferences process
        /*SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        Toast.makeText(this,
                "phone number is " + sharedPreferences.getString(Login.PHONENUMBER, "")
                , Toast.LENGTH_LONG).show();*/
        ///End of to do
    }
}