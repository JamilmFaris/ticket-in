package com.example.busreservationsystem.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class Trips extends AppCompatActivity {

    TripsAdapter tripsAdapter;
    RecyclerView tripsView;
    ArrayList<Trip> trips = new ArrayList<>();
    RequestQueue queue;
    ClickListener tripListener;
    String getTripsUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        // init
        tripsView = findViewById(R.id.trips);
        getTripsUrl = "http://127.0.0.1:8000/api/v1/trips";
        //

        tripListener = new ClickListener() {
            @Override
            public void click(int index) {
                //TODO : dialog when item clicked
                Toast.makeText(Trips.this, "you clicked " + index + " item",
                        Toast.LENGTH_SHORT).show();
                bookTrip(index);
            }
        };
        tripsAdapter = new TripsAdapter(tripListener);
        //
        tripsView.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                1, RecyclerView.VERTICAL, false));

        queue = Volley.newRequestQueue(this);
        getTrips();
        /*trips.add(new Trip("jamil"));trips.add(new Trip("jamil1"));
        trips.add(new Trip("jamil2"));trips.add(new Trip("jamil3"));
        tripsAdapter.addItems(trips);*/
        tripsView.setAdapter(tripsAdapter);

    }

    public void getTrips(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getTripsUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray tripsJSONArray = response.getJSONObject(0).getJSONArray("trips");
                            for(int i = 0;i < tripsJSONArray.length();i++){
                                    JSONObject curTrip = tripsJSONArray.getJSONObject(i);
                                    int id =  curTrip.getInt("id");
                                    JSONObject curTripCompany = curTrip.getJSONObject("company");

                                    String companyName = curTripCompany.getString("name");
                                    String from = curTrip.getJSONObject("source").getString("name");
                                    String to = curTrip.getJSONObject("destination").getString("name");
                                    int price = curTrip.getInt("price");
                                    String startAt = curTrip.getString("start_at");
                                    Trip cur = new Trip(id, companyName, "from " + from + " to " + to,
                                            startAt, price);
                                    trips.add(cur);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Trips.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                        tripsAdapter.addItems(trips);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Trips.this, "volley " + error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(jsonArrayRequest);
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
                String bookTripUrl = "";
                JSONObject tripObject = new JSONObject();
                JsonArrayRequest bookTripArrayRequest = new JsonArrayRequest(Request.Method.POST, bookTripUrl, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Trips.this,
                                "the trip is not booked", Toast.LENGTH_SHORT).show();
                    }
                });
                bookTripArrayRequest.
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