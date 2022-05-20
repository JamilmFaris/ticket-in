package com.example.busreservationsystem.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.busreservationsystem.Adapter.TripsAdapter;
import com.example.busreservationsystem.ClickListener.ClickListener;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.Models.Trip;
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trips extends AppCompatActivity {

    TripsAdapter tripsAdapter;
    RecyclerView tripsView;
    ArrayList<Trip> trips = new ArrayList<>();
    RequestQueue queue;
    ClickListener tripListener;
    String getTripsUrl;
    Map<String, String> companyHandleMap =  new HashMap<>();
    Map<String, String> citySlugMap =  new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        // init
        tripsView = findViewById(R.id.trips);
        getTripsUrl = "http://192.168.43.239/ticket-in-backend/public/api/v1/trips";
        //
        queue = Volley.newRequestQueue(this);

        tripListener = new ClickListener() {
            @Override
            public void click(int index) {
                //TODO : dialog when item clicked and remove toast
                Toast.makeText(Trips.this, "you clicked " + index + " item",
                        Toast.LENGTH_SHORT).show();
                bookTrip(index);
            }
        };
        tripsAdapter = new TripsAdapter(tripListener);
        //
        tripsView.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                1, RecyclerView.VERTICAL, false));
        getTrips();



        /*trips.add(new Trip("jamil"));trips.add(new Trip("jamil1"));
        trips.add(new Trip("jamil2"));trips.add(new Trip("jamil3"));
        tripsAdapter.addItems(trips);*/
        tripsView.setAdapter(tripsAdapter);

    }

    public void getTrips(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , getTripsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Trip> curTrips = new ArrayList<>();
                        try {
                            JSONArray tripsJSONArray = response.getJSONArray("trips");
                            for(int i = 0;i < tripsJSONArray.length();i++){
                                    JSONObject curTrip = tripsJSONArray.getJSONObject(i);
                                    int id =  curTrip.getInt("id");

                                    JSONObject curTripCompany = curTrip.getJSONObject("company");

                                    String companyName = curTripCompany.getString("name");
                                    String companyHandle = curTripCompany.getString("handle");
                                    companyHandleMap.put(companyName, companyHandle);


                                    JSONObject curTripSource = curTrip.getJSONObject("source");
                                    String sourceName = curTripSource.getString("name");
                                    String sourceSlug = curTripSource.getString("slug");
                                    citySlugMap.put(sourceName, sourceSlug);


                                    JSONObject curTripDestination = curTrip.getJSONObject("destination");
                                    String destinationName = curTripDestination.getString("name");
                                    String destinationSlug = curTripDestination.getString("slug");
                                    citySlugMap.put(sourceName, sourceSlug);


                                    int price = curTrip.getInt("price");

                                    String startAt = curTrip.getString("start_at");


                                    boolean was_booked = curTrip.getBoolean("was_booked");


                                    Trip cur = new Trip(id, companyName,sourceName, destinationName,
                                            startAt, price, was_booked);
                                    curTrips.add(cur);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Trips.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                        trips.clear();
                        trips.addAll(curTrips);
                        tripsAdapter.addItems(trips);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Trips.this, "volley " + error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                ){
            @Override
            public Map<String,String> getHeaders() {
                //TODO : change bearer
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " +  MainActivity.token );
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }


    public void bookTrip(int id){
        // TODO : put it in booked trips in profile
        AlertDialog.Builder builder = new AlertDialog.Builder(Trips.this);
        builder.setCancelable(true);
        builder.setTitle("Approve booking");
        builder.setMessage("Do you want to book this trip");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String bookTripUrl = "http://192.168.43.239/ticket-in-backend/public/api/v1/trips/";
                bookTripUrl += id;
                bookTripUrl += "/book";
                JSONObject tripObject = new JSONObject();
                JsonArrayRequest bookTripArrayRequest = new JsonArrayRequest(Request.Method.POST
                        , bookTripUrl, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Toast.makeText(Trips.this
                                        , "trip booked"  , Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Trips.this,
                                "the trip is not booked", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public Map<String,String> getHeaders() {
                        //TODO : change bearer
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + MainActivity.token );
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
}