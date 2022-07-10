package com.example.busreservationsystem.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.busreservationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    String TAG = "profile";

    TextView firstName, lastName, phoneNumber;
    RecyclerView bookedTripsView;
    TripsAdapter tripsAdapter ;
    ArrayList<Trip> trips = new ArrayList<>();
    String bookTripUrl;
    String bookedTripsUrl ;
    String cancelTripUrl = "";
    RequestQueue queue ;
    Passenger passenger;
    Button editProfileButton;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //initialize
        firstName = findViewById(R.id.first_name_profile);
        lastName = findViewById(R.id.last_name_profile);
        phoneNumber = findViewById(R.id.phone_number_profile);
        bookedTripsView = findViewById(R.id.booked_trips);
        queue = Volley.newRequestQueue(this);
        editProfileButton = findViewById(R.id.edit_profile_button);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerUtil.getDrawer(this,toolbar);

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
        passenger = Helper.loadPassenger(this);
        bookedTripsView.setAdapter(tripsAdapter);
        getBookedTrips();

        firstName.setText(passenger.getFirstName());
        lastName.setText(passenger.getLastName());
        phoneNumber.setText(passenger.getPhoneNumber());


        //edit profile
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });

    }
    public void getBookedTrips(){// /4/trips/upcoming
        bookedTripsUrl = Url.getBookedTripUrl(passenger.getId());
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

                            Trip cur = new Trip(id, companyName, sourceName ,  destinationName,startAt,
                                    price, was_booked);
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
                    Helper.onAuthFailureError(Profile.this, error);

                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " +  Helper.loadToken(Profile.this));
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
                cancelTripUrl = Url.getCancelTripUrl(trips.get(index).id);

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
                                Helper.onAuthFailureError(Profile.this,error);

                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " +  Helper.loadToken(Profile.this));
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


    public void bookTrip(int tripIndex){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setCancelable(true);
        builder.setTitle("Approve booking");
        builder.setMessage("Do you want to book this trip");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bookTripUrl = Url.getBookTripUrl(trips.get(tripIndex).id);


                ///
                StringRequest bookTripRequest = new StringRequest(Request.Method.POST,
                        bookTripUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(Profile.this,
                                        "the trip is booked", Toast.LENGTH_SHORT).show();
                                trips.get(tripIndex).was_booked = true;
                                tripsAdapter.notifyItemChanged(tripIndex);
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Helper.onAuthFailureError(Profile.this,error);


                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " +  Helper.loadToken(Profile.this));
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

    public void editProfile(){
        //pops up a window to let the user input new sheet's information
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Edit my profile");
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(editLayout);
        EditText firstNameText = findViewById(R.id.edit_first_name),
                lastNameText = findViewById(R.id.edit_last_name),
                phoneNumberText = findViewById(R.id.edit_phone_number),
                previousPasswordText = findViewById(R.id.edit_previous_password),
                passwordText = findViewById(R.id.edit_new_password),
                passwordConfirmationText = findViewById(R.id.edit_new_password_confirmation);
        builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(firstNameText.getText().toString().isEmpty() ||
                lastNameText.getText().toString().isEmpty() ||
                phoneNumberText.getText().toString().isEmpty() ||
                previousPasswordText.getText().toString().isEmpty() ||
                passwordText.getText().toString().isEmpty() ||
                passwordConfirmationText.getText().toString().isEmpty()){
                    Toast.makeText(Profile.this, "enter missing data", Toast.LENGTH_SHORT).show();
                }
                else if(!passwordText.getText().toString().equals(passwordConfirmationText.getText().toString())){
                    Toast.makeText(Profile.this
                            , getResources().getString(R.string.password_doesnt_match)
                            , Toast.LENGTH_SHORT).show();
                }
                else{
                    String editFirstName = firstNameText.getText().toString(), editLastName = lastNameText.getText().toString(),
                    editPhoneNumber = phoneNumberText.getText().toString(), editPassword = passwordText.getText().toString(),
                    editPasswordConfirmation = passwordConfirmationText.getText().toString();

                    // todo json object request
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Trips.class);
        startActivity(intent);
    }
}