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
import com.example.busreservationsystem.Helper.Helper;
import com.example.busreservationsystem.Helper.Url;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.Models.Passenger;
import com.example.busreservationsystem.Models.Trip;
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    String TAG = "login";
    EditText phoneNumber;
    EditText password;
    Button login;
    RequestQueue queue;
    String url;
    public static String PHONENUMBER = "PhoneNumber";
    public static String PASSWORD = "password";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /// initialize
        url = Url.getLoginUrl();
        queue = Volley.newRequestQueue(this);
        phoneNumber = findViewById(R.id.passenger_phonenumber_login);
        password = findViewById(R.id.passenger_password_login);
        login = findViewById(R.id.login);

        intent = new Intent(this, Trips.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNumber.getText().toString().isEmpty() ||
                    password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter some data"
                            , Toast.LENGTH_SHORT).show();
                }
                else if(!Helper.isPhoneNumber( phoneNumber.getText().toString() ).isEmpty()){
                    Toast.makeText(getApplicationContext()
                            , Helper.isPhoneNumber( phoneNumber.getText().toString() )
                            , Toast.LENGTH_SHORT).show();
                }
                else{
                    loginData(phoneNumber.getText().toString()
                            , password.getText().toString());
                }
            }
        });
    }


    public void loginData(String phoneNumber, String password){
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("phone_number", phoneNumber);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST
                , url, loginData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Logged in successfully"
                                , Toast.LENGTH_SHORT).show();

                        Passenger passenger ;
                        try {
                            JSONObject passengerObject = response.getJSONObject("passenger");
                            passenger = new Passenger(passengerObject.getInt("id")
                                    , passengerObject.getString("first_name")
                                    , passengerObject.getString("last_name")
                                    , passengerObject.getString("phone_number"));
                            String token = response.getString("token");
                            intent.putExtra("passenger", passenger);
                            intent.putExtra("token", token);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Toast.makeText(Login.this, "a problem occured"
                                    , Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = Helper.onErrorResponse(error);
                        if(!message.isEmpty()){
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        }
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
    }
}