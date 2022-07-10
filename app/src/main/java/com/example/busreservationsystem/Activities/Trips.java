package com.example.busreservationsystem.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.busreservationsystem.Adapter.TripsAdapter;
import com.example.busreservationsystem.ClickListener.ClickListener;
import com.example.busreservationsystem.Helper.DrawerUtil;
import com.example.busreservationsystem.Helper.Helper;
import com.example.busreservationsystem.Helper.Url;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.Models.Passenger;
import com.example.busreservationsystem.Models.Trip;
import com.example.busreservationsystem.Notification.Notification;
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Trips extends AppCompatActivity {
    String TAG = "trips";
    public static int notificationId = 1;

    TripsAdapter tripsAdapter;
    RecyclerView tripsView;
    ArrayList<Trip> trips = new ArrayList<>();
    RequestQueue queue;
    ClickListener tripListener;
    String getTripsUrl;
    String bookTripUrl;
    String cancelTripUrl;
    String getCompaniesHandleUrl;
    String getCitiesSlugUrl;
    Map<String, String> companyHandleMap = new HashMap<>(); // key and value are in lower case only
    ArrayList<String> companies = new ArrayList<>();
    ArrayList<String> cities = new ArrayList<>();
    Map<String, String> citySlugMap = new HashMap<>();
    AutoCompleteTextView companyFilter, sourceFilter, destinationFilter;
    ArrayAdapter<String> companyFilterAdapter;
    ArrayAdapter<String> sourceFilterAdapter;
    ArrayAdapter<String> destinationFilterAdapter;
    String selectedCompanyHandle = "";
    String selectedSourceSlug = "";
    String selectedDestinationSlug = "";
    NestedScrollView nestedScrollView;
    int tripsPage = 0, limit;
    String token;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        // init
        tripsView = findViewById(R.id.trips);
        nestedScrollView = findViewById(R.id.nested_scroll_view);

        getCompaniesHandleUrl = Url.getGetCompaniesHandleUrl();
        getCitiesSlugUrl = Url.getGetCitiesSlugUrl();
        companyFilter = findViewById(R.id.company_filter);
        sourceFilter = findViewById(R.id.source_filter);
        destinationFilter = findViewById(R.id.destination_filter);
        queue = Volley.newRequestQueue(this);
        /*toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("toolbar");
        setSupportActionBar(toolbar);

        DrawerUtil.getDrawer(this,toolbar);*/

        //



        tripListener = new ClickListener() {
            @Override
            public void click(int index) {
                //index from trips array
                if(trips.get(index).was_booked){
                    cancelBooking(index);
                }
                else{
                    bookTrip(index);
                }

            }
        };
        ///filter
        companyFilterAdapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, companies);
        companyFilter.setThreshold(0);
        companyFilter.setTextColor(Color.RED);
        companyFilter.setAdapter(companyFilterAdapter);

        companyFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCompanyHandle = adapterView.getItemAtPosition(i).toString().toLowerCase(Locale.ROOT) ;
                selectedCompanyHandle = companyHandleMap.get(selectedCompanyHandle);
                getTrips(selectedCompanyHandle, selectedSourceSlug, selectedDestinationSlug);
            }
        });
        companyFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(companyFilter.getText().toString().isEmpty()){
                    selectedCompanyHandle = "";
                    getTrips(selectedCompanyHandle, selectedSourceSlug, selectedDestinationSlug);
                }
            }
        });
        companyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyFilter.showDropDown();
            }
        });




        sourceFilterAdapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, cities);
        sourceFilter.setThreshold(1);
        sourceFilter.setTextColor(Color.RED);
        sourceFilter.setAdapter(sourceFilterAdapter);
        sourceFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSourceSlug = (adapterView.getItemAtPosition(i).toString().toLowerCase(Locale.ROOT)) ;
                selectedSourceSlug = citySlugMap.get(selectedSourceSlug);
                getTrips(selectedCompanyHandle, selectedSourceSlug, selectedDestinationSlug);
            }
        });
        sourceFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(sourceFilter.getText().toString().isEmpty()){
                    selectedSourceSlug = "";
                    getTrips(selectedCompanyHandle, selectedSourceSlug, selectedDestinationSlug);
                }

            }
        });
        sourceFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceFilter.showDropDown();
            }
        });


        destinationFilterAdapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, cities);
        destinationFilter.setThreshold(1);
        destinationFilter.setTextColor(Color.RED);
        destinationFilter.setAdapter(destinationFilterAdapter);
        destinationFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDestinationSlug = (adapterView.getItemAtPosition(i).toString().toLowerCase(Locale.ROOT)) ;
                selectedDestinationSlug = citySlugMap.get(selectedDestinationSlug);
                getTrips(selectedCompanyHandle, selectedSourceSlug, selectedDestinationSlug);
            }
        });
        destinationFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(destinationFilter.getText().toString().isEmpty()){
                    selectedDestinationSlug = "";
                    getTrips(selectedCompanyHandle, selectedSourceSlug, selectedDestinationSlug);
                }

            }
        });
        destinationFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationFilter.showDropDown();
            }
        });
        ///filter


        tripsAdapter = new TripsAdapter(tripListener);
        //
        tripsView.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                1, RecyclerView.VERTICAL, false));


        getTrips("", "", "");


        /*trips.add(new Trip("jamil"));trips.add(new Trip("jamil1"));
        trips.add(new Trip("jamil2"));trips.add(new Trip("jamil3"));
        tripsAdapter.addItems(trips);*/
        tripsView.setAdapter(tripsAdapter);

        //nestedScrollView
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    tripsPage++;
                    getTripsPagination(tripsPage, selectedCompanyHandle,
                            selectedSourceSlug, selectedDestinationSlug);
                }
            }
        });



    }



    ///////////////////functions /////////////////////

    public void getTrips(String companyHandleParam
            , String sourceSlugParam, String destinationSlugParam){
        tripsPage = 0;
        getTripsPagination(0, companyHandleParam, sourceSlugParam, destinationSlugParam);
    }

    public void getTripsPagination(int page, String companyHandleParam
            , String sourceSlugParam, String destinationSlugParam){// parameters are handle and slugs

        getTripsUrl = Url.getGetTripsUrl(companyHandleParam, sourceSlugParam, destinationSlugParam);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , getTripsUrl, null,
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
                                citySlugMap.put(destinationName.toLowerCase(Locale.ROOT), destinationSlug);


                                int price = curTrip.getInt("price");
                                String startAt = curTrip.getString("start_at");

                                boolean was_booked = curTrip.getBoolean("was_booked");

                                Trip cur = new Trip(id, companyName, sourceName ,  destinationName,startAt,
                                         price, was_booked);
                                trips.add(cur);
                        }
                        tripsAdapter.addItems(trips);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Helper.onAuthFailureError(Trips.this, error);
                }){
            @Override
            public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " +  token);
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void bookTrip(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(Trips.this);
        builder.setCancelable(true);
        builder.setTitle("Approve booking");
        builder.setMessage("Do you want to book this trip");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bookTripUrl = Url.getBookTripUrl(trips.get(index).id);


                ///
                StringRequest bookTripRequest = new StringRequest(Request.Method.POST,
                        bookTripUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(Trips.this,
                                        "the trip is booked", Toast.LENGTH_SHORT).show();
                                trips.get(index).was_booked = true;

                                // TODO change notificationId
                                Trip curTrip = trips.get(index);
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, curTrip.year);
                                cal.set(Calendar.MONTH, curTrip.month );// is subtracted by one in the Trip class
                                cal.set(Calendar.DAY_OF_MONTH, curTrip.day );
                                cal.set(Calendar.HOUR, curTrip.hour);
                                cal.add(Calendar.HOUR, -12);
                                cal.set(Calendar.MINUTE, curTrip.min);
                                cal.set(Calendar.SECOND, curTrip.sec);
                                Notification.setAlert(Trips.this, cal
                                        , notificationId++, "trip notification",
                                        "you have a trip after 30 minutes");
                                tripsAdapter.notifyItemChanged(index);
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Helper.onAuthFailureError(Trips.this, error);
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " +  token);
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

    public void cancelBooking(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(Trips.this);
        builder.setCancelable(true);
        builder.setTitle("Approve cancelling");
        builder.setMessage("Do you want to cancel this trip");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelTripUrl = Url.getCancelTripUrl(trips.get(index).id);

                StringRequest bookTripArrayRequest = new StringRequest(Request.Method.POST
                        , cancelTripUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(Trips.this
                                        , R.string.trip_is_cancelled, Toast.LENGTH_SHORT).show();
                                trips.get(index).was_booked = false;
                                tripsAdapter.notifyItemChanged(index);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Helper.onAuthFailureError(Trips.this, error);

                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " +  token);
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

    public void getCitiesSlug(){// gets cities and initialize citySlugMap
        JsonObjectRequest getCitiesSlugRequest = new JsonObjectRequest(Request.Method.GET
                , getCitiesSlugUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray citiesJsonArray = response.getJSONArray("cities");
                            for(int i = 0;i < citiesJsonArray.length();i++){
                                JSONObject curCityObject = citiesJsonArray.getJSONObject(i);

                                String curCityName = curCityObject.getString("name");
                                String curCitySlug = curCityObject.getString("slug");

                                citySlugMap.put(curCityName, curCitySlug);
                                cities.add(curCityName);
                            }
                            sourceFilterAdapter.notifyDataSetChanged();
                            destinationFilterAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Helper.onAuthFailureError(Trips.this, error);

                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " +  token);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        queue.add(getCitiesSlugRequest);
    }

    public void getCompaniesHandle(){// gets companies and initialize companyHandleMap
        JsonObjectRequest getCompaniesHandleRequest = new JsonObjectRequest(Request.Method.GET
                , getCompaniesHandleUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray companiesJsonArray = response.getJSONArray("companies");
                            for(int i = 0;i < companiesJsonArray.length();i++){
                                JSONObject curCompanyObject = companiesJsonArray.getJSONObject(i);

                                String curCompanyName = curCompanyObject.getString("name");
                                String curCompanyHandle = curCompanyObject.getString("handle");
                                companyHandleMap.put(curCompanyName.toLowerCase(Locale.ROOT), curCompanyHandle);
                                companies.add(curCompanyName);
                            }
                            companyFilterAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Helper.onAuthFailureError(Trips.this, error);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " +  token);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        queue.add(getCompaniesHandleRequest);

    }

    @Override
    protected void onPause() {
        Helper.saveToken(this, token);
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        getCitiesSlug();
        getCompaniesHandle();
        token = Helper.loadToken(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerUtil.getDrawer(this,toolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.removeToken(this);
    }
}