package com.vlo.vitalii.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;

public class RideConfirmation extends KinveyActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_confirmation);
        final Button hreturn = findViewById(R.id.homeReturn);
        final Button logout = findViewById(R.id.logoutBtn);
        Bundle extras = getIntent().getExtras();
        final String pickupAddress = extras.getString("PickupLocation");
        String text = extras.getString("RideDetails");
        textView = findViewById(R.id.text_RideDetails);
        textView.setText(" ");
        String[] RideDetails = text.split("-");
        String carno = RideDetails[1].trim();
        String source = RideDetails[3].trim();
        String destination = RideDetails[5].trim();
        String time = RideDetails[7].trim();

        Query myQuery = getClient().query();
        myQuery.equals("Source", source);
        myQuery.equals("Destination", destination);
        myQuery.equals("RideTime", time);
        myQuery.equals("CarNumber", carno);

        final AsyncAppData<RideInfo> myData = getClient().appData("RideInfo", RideInfo.class);
        myData.get(myQuery, new KinveyListCallback<RideInfo>() {

            @Override
            public void onSuccess(RideInfo[] rideInfos) {

                if (rideInfos.length > 0) {
                    textView.setText(String.format("\nRIDE DETAILS\nDriver Details -\n%s\nPHONE NO-%s\nCAR NO- %s\nSource- %s\nDestination- %s\nTime-%s\n\n\nPlease contact the driver for any ride queries.", rideInfos[0].getUserID(), rideInfos[0].getPhone_Num(), rideInfos[0].getCar_Num(), rideInfos[0].getSource(), rideInfos[0].getDestination(), rideInfos[0].getRideTime()));
                    rideInfos[0].setRideStatus("Booked");

                    //RideInfo rideInfo = new RideInfo();
                    String to = rideInfos[0].getUserID();
                    String to2 = getClient().user().toString();
                    String rt = getClient().user().toString();
                    String body = " from " + rideInfos[0].getSource() + " to " + rideInfos[0].getDestination() + " at time " + rideInfos[0].getRideTime() + "\n\n" + pickupAddress;

                    RideInfo ride = new RideInfo();
                    ride.setTo(to);
                    ride.setTo2(to2);
                    ride.setBody(body);
                    ride.setReplyTo(rt);
                    ride.setSubject("Ride Details");
                    ride.setSource(rideInfos[0].getSource());
                    ride.setDestination(rideInfos[0].getDestination());
                    ride.setRideTime(rideInfos[0].getRideTime());
                    ride.setPhone_Num(rideInfos[0].getPhone_Num());
                    ride.setUserID(getClient().user().toString());
                    ride.setCar_Num(rideInfos[0].getCar_Num());
                    ride.setRideStatus("Booked");

                    AsyncAppData<RideInfo> myData2 = getClient().appData("RideInfo", RideInfo.class);
                    myData2.save(ride, new KinveyClientCallback<RideInfo>() {
                        @Override
                        public void onSuccess(RideInfo rideInfo) {
                            Log.v("TAG", "Ride booked");
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.v("TAG", "Faailed to book");
                        }
                    });


                    AsyncAppData<RideInfo> myData1 = getClient().appData("RideInfo", RideInfo.class);
                    myData1.save(rideInfos[0], new KinveyClientCallback<RideInfo>() {

                        @Override
                        public void onSuccess(RideInfo rideInfo) {

                            Toast.makeText(RideConfirmation.this, "Ride Sucessfully Booked..", Toast.LENGTH_SHORT).show();
                            Log.v("TAG", "Ride booked");
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.v("TAG", "Filed to book");
                        }
                    });
                } else {
                    textView.setText("Something missing");
                }

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });


        hreturn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), HomeActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });


        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

    }
}
