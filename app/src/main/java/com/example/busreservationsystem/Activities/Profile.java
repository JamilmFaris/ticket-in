package com.example.busreservationsystem.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.busreservationsystem.Adapter.TripsAdapter;
import com.example.busreservationsystem.ClickListener.ClickListener;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.Models.Passenger;
import com.example.busreservationsystem.Models.Trip;
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Profile extends AppCompatActivity {
    String TAG = "profile";

    TextView name, phoneNumber;
    RecyclerView bookedTripsView;
    TripsAdapter tripsAdapter ;
    ArrayList<Trip> trips = new ArrayList<>();
    String bookTripUrl;
    String bookedTripsUrl = "http://192.168.1.102/ticket-in-backend/public/api/v1/passengers";
    String cancelTripUrl = "";
    RequestQueue queue ;
    Passenger passenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //initialize
        name = findViewById(R.id.name_profile);
        phoneNumber = findViewById(R.id.phone_number_profile);
        bookedTripsView = findViewById(R.id.booked_trips);
        queue = Volley.newRequestQueue(this);

        //

        ClickListener listener = new ClickListener() {
            @Override
            public void click(int index) {
                if(trips.get(index).was_booked){
                    cancelBooking(index);
                }
                else{
                    bookTrip(index);
                }
            }
        };
        tripsAdapter = new TripsAdapter(listener);

        bookedTripsView.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                1, RecyclerView.VERTICAL, false));
        passenger =(Passenger) getIntent().getSerializableExtra("passenger");
        bookedTripsView.setAdapter(tripsAdapter);
        getBookedTrips();

        name.setText(passenger.getFirstName());
        phoneNumber.setText(passenger.getPhoneNumber());



    }
    public void getBookedTrips(){// /4/trips/upcoming
        bookedTripsUrl = "http://192.168.1.102/ticket-in-backend/public/api/v1/passengers";
        bookedTripsUrl += "/" + passenger.getId();
        bookedTripsUrl += "/trips/upcoming";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , bookedTripsUrl, null,
                response -> {
                    try {
                        trips.clear();
                        JSONArray tripsJSONArray = response.getJSONArray("trips");
                        for(int i = 0;i < tripsJSONArray.length();i++){
                            JSONObject curTrip = tripsJSONArray.getJSONObject(i);
                            int id =  curTrip.getInt("id");


                            JSONObject curTripCompany = curTrip.getJSONObject("company");
                            String companyName = curTripCompany.getString("name");
                            String companyHandle = curTripCompany.getString("handle");


                            JSONObject source = curTrip.getJSONObject("source");
                            String sourceName = source.getString("name");
                            String sourceSlug = source.getString("slug");


                            JSONObject destination = curTrip.getJSONObject("destination");
                            String destinationName = destination.getString("name");
                            String destinationSlug = destination.getString("slug");



                            int price = curTrip.getInt("price");
                            String startAt = curTrip.getString("start_at");

                            boolean was_booked = curTrip.getBoolean("was_booked");

                            Trip cur = new Trip(id, companyName, sourceName ,  destinationName,
                                    price, was_booked);
                            cur.setStartAt(startAt);
                            trips.add(cur);
                        }
                        tripsAdapter.addItems(trips);
                        Log.d(TAG, "getBookedTrips: trips added" );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "getBookedTrips: " + e);
                    }
                },
                error -> {
                    if(error instanceof AuthFailureError){
                        //token is not valid
                        MainActivity.token = "";
                        startActivity(new Intent(Profile.this, MainActivity.class));
                    }
                    Toast.makeText(Profile.this
                            ,error.toString(), Toast.LENGTH_SHORT).show();
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " +  getIntent().getStringExtra("token"));
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void cancelBooking(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setCancelable(true);
        builder.setTitle("Approve cancelling");
        builder.setMessage("Do you want to cancel this trip");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelTripUrl = "http://192.168.1.102/ticket-in-backend/public/api/v1/trips";
                cancelTripUrl += "/" + trips.get(index).id;
                cancelTripUrl += "/cancelBook";

                StringRequest bookTripArrayRequest = new StringRequest(Request.Method.POST
                        , cancelTripUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(Profile.this
                                        , R.string.trip_is_cancelled, Toast.LENGTH_SHORT).show();
                                trips.get(index).was_booked = false;
                                tripsAdapter.notifyItemChanged(index);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Profile.this,
                                        "the trip is not cancelled", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onErrorResponse: " + error.toString());
                                if(error instanceof AuthFailureError){
                                    //token is not valid
                                    MainActivity.token = "";
                                    startActivity(new Intent(Profile.this, MainActivity.class));
                                }
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " +  getIntent().getStringExtra("token"));
                        headers.put("Content-Type", "application/json");
                        headers.put("Accept", "application/json");
                        return headers;
                    }
                };
                queue.add(bookTripArrayRequest);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void bookTrip(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setCancelable(true);
        builder.setTitle("Approve booking");
        builder.setMessage("Do you want to book this trip");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bookTripUrl = "http://192.168.1.102/ticket-in-backend/public/api/v1/trips";
                bookTripUrl += "/" + trips.get(index).id;
                bookTripUrl += "/book";


                ///
                StringRequest bookTripRequest = new StringRequest(Request.Method.POST,
                        bookTripUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(Profile.this,
                                        "the trip is booked", Toast.LENGTH_SHORT).show();
                                trips.get(index).was_booked = true;
                                tripsAdapter.notifyItemChanged(index);
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof AuthFailureError){
                            //token is not valid
                            MainActivity.token = "";
                            startActivity(new Intent(Profile.this, MainActivity.class));
                        }
                        Toast.makeText(Profile.this,
                                "the trip is not booked error is " + error.toString()
                                , Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " +  getIntent().getStringExtra("token"));
                        headers.put("Content-Type", "application/json");
                        headers.put("Accept", "application/json");
                        return headers;
                    }
                };
                queue.add(bookTripRequest);
                ///

            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Trips.class);
        intent.putExtra("passenger", passenger);
        String token = getIntent().getStringExtra("token");
        intent.putExtra("token", token);
        startActivity(intent);
    }
}