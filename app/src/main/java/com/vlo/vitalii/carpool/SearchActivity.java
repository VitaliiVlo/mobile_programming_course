package com.vlo.vitalii.carpool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;


public class SearchActivity extends KinveyActivity {
    private TextView get_place;
    String address;
    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final Button pickLocation = findViewById(R.id.btn_pick);
        final EditText Destination = findViewById(R.id.edit_Dest);

        final Button SearchRide = findViewById(R.id.btn_Search);
        final Button BookRide = findViewById(R.id.bookBtn);
        final Button cancel = findViewById(R.id.btn_SCancel);

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), HomeActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMap();
            }


        });

        assert SearchRide != null;
        SearchRide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Query myQuery = getClient().query();
                myQuery.equals("RideStatus", "Available");
                myQuery.equals("Destination", Destination.getText().toString());
                AsyncAppData<RideInfo> myData = getClient().appData("RideInfo", RideInfo.class);
                myData.get(myQuery, new KinveyListCallback<RideInfo>() {
                    @Override
                    public void onSuccess(RideInfo[] rideInfos) {
                        Log.v("TAG", "received " + rideInfos.length + " events");
                        addRadioButton(rideInfos);
                        BookRide.setVisibility(BookRide.VISIBLE);
                        //  RadioGroup rg1 = new RadioGroup();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.v("TAG", "Failed");
                    }
                });
            }
        });

        BookRide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayConfirmation();
            }
        });

    }


    private void displayMap() {
        get_place = findViewById(R.id.textView9);
        get_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build((Activity) getApplicationContext());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                address = String.format("Pickup Location: %s", place.getAddress());
                get_place.setText(address);
                setAddress(address);

            }
        }
    }

    private void displayConfirmation() {

        RadioGroup radioGroup = findViewById(R.id.RadioButtonGroup);
        if (radioGroup.getChildCount() > 0) {
            RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
            String selectedtext = radioButton.getText().toString();
            Toast.makeText(SearchActivity.this, selectedtext, Toast.LENGTH_SHORT).show();

            //Toast.makeText(SearchActivity.this, text[5], Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(this, RideConfirmation.class);
            myIntent.putExtra("RideDetails", selectedtext);
            myIntent.putExtra("PickupLocation", address);
            startActivityForResult(myIntent, 0);
            finish();

        } else {
            Toast.makeText(SearchActivity.this, "No Rides available to book.. Please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void addRadioButton(RideInfo[] rideInfos) {

        RadioGroup radioGroup = findViewById(R.id.RadioButtonGroup);
        RadioGroup.LayoutParams rprms;
        radioGroup.removeAllViews();

        if (rideInfos.length > 0) {
            for (int i = 0; i < (rideInfos.length); i++) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(String.format("CAR NO- %s -Source- %s -Destination- %s -Time- %s", rideInfos[i].getCar_Num(), rideInfos[i].getSource(), rideInfos[i].getDestination(), rideInfos[i].getRideTime()));
                radioButton.setId(i);
                rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                radioGroup.addView(radioButton, rprms);
            }
        } else {
            Toast.makeText(SearchActivity.this, "No Rides available to destination.. Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
