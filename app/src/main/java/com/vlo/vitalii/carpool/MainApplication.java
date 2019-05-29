
package com.vlo.vitalii.carpool;

import android.app.Application;

import com.kinvey.android.Client;

public class MainApplication extends Application {
    private Client client;

    @Override
    public void onCreate() {
        super.onCreate();
        defineClient();
    }

    private void defineClient() {
        client = new Client.Builder("kid_S1UwBEj5E",//APP_ID
                "e45bf272498c47418ee0beeec74541e8",//APP_SECRET
                getApplicationContext()).build();
    }

    public Client getClient() {
        return client;
    }
}
