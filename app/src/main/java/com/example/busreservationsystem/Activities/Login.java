package com.example.busreservationsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class Login extends AppCompatActivity {

    EditText phoneNumber;
    EditText password;
    Button login;
    RequestQueue queue;
    String url;
    public static String PHONENUMBER = "PhoneNumber";
    public static String PASSWORD = "password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /// initialize
        // TODO : change the url
        url = "/api/companies/login";
        queue = Volley.newRequestQueue(this);
        phoneNumber = findViewById(R.id.passenger_phonenumber_login);
        password = findViewById(R.id.passenger_password_login);
        login = findViewById(R.id.login);

        //TODO : change the intent where do you need to go

        Intent intent = new Intent(this, MainActivity.class);
        // if logged in before // there is a problem {when you need to login to a new account
        loadLoggedStatus();
        if(!phoneNumber.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            startActivity(intent);
        }

        //else wait for the login button

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNumber.getText().toString().isEmpty() ||
                    password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter some data"
                            , Toast.LENGTH_SHORT).show();
                }
                else{

                    loginData(phoneNumber.getText().toString(), password.getText().toString());
                    MainActivity.login.setVisibility(View.GONE);
                    MainActivity.signup.setVisibility(View.GONE);

                }
            }
        });
    }


    public void loginData(String phoneNumber, String password){
        JSONObject logindata = new JSONObject();
        try {
            logindata.put("phoneNumber", phoneNumber);

        logindata.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(logindata);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(getApplicationContext(), "Logged in successfully"
                                , Toast.LENGTH_SHORT).show();
                        /// TODO : get the id of the passenger
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
    }

    public void saveLoggedStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PHONENUMBER, phoneNumber.getText().toString());
        editor.putString(PASSWORD, password.getText().toString());
    }
    public void loadLoggedStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        phoneNumber.setText( sharedPreferences.getString(PHONENUMBER, "") );
        password.setText(sharedPreferences.getString(PASSWORD, ""));
    }
}