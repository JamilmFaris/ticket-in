package com.example.busreservationsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.Models.Passenger;
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Signup extends AppCompatActivity {
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    EditText password;
    Button signup;
    RequestQueue queue;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        queue = Volley.newRequestQueue(this);
        // TODO : change the url
        url = "/api/passengers/signup";
        firstName = findViewById(R.id.passenger_firstname);
        lastName = findViewById(R.id.passenger_lastname);
        phoneNumber = findViewById(R.id.passenger_phonenumber);
        password = findViewById(R.id.passenger_password);
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() || password.getText().toString().isEmpty() ){
                    Toast.makeText(getApplicationContext(), "Enter some data", Toast.LENGTH_SHORT).show();
                }
                else{
                    Passenger passenger = new Passenger(firstName.getText().toString(), lastName.getText().toString()
                            , phoneNumber.getText().toString(), password.getText().toString());
                    MainActivity.signup.setVisibility(View.GONE);
                    MainActivity.login.setVisibility(View.GONE);
                    postPassenger(passenger);

                }
            }
        });
    }


    public void postPassenger(Passenger passenger){
        JSONObject passengerObject = new JSONObject();
        try {
            passengerObject.put("id", passenger.getId());
            passengerObject.put("firstName", passenger.getFirstName());
            passengerObject.put("lastName", passenger.getLastName());
            passengerObject.put("phoneNumber", passenger.getPhoneNumber());
            passengerObject.put("password", passenger.getPassword());
        }catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Signup.this,
                    "volley object error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(passengerObject);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(getApplicationContext(), "Registered"
                                , Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Problem " + error.toString()
                                , Toast.LENGTH_SHORT).show();
                    }
        });
        queue.add(jsonArrayRequest);
        // TODO : try the shared preferences process
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        Toast.makeText(this,
                "phone number is " + sharedPreferences.getString(Login.PHONENUMBER, "")
                , Toast.LENGTH_LONG).show();
        ///End of to do
    }
}