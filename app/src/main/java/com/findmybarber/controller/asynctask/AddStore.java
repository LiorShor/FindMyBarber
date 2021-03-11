package com.findmybarber.controller.asynctask;

import android.os.AsyncTask;

import com.findmybarber.model.Store;

public class AddStore extends AsyncTask<Void, Void, Void> {

    private Store store;
    private String emailClient;

    public AddStore(Store store, String emailClient) {
        this.store = store;
        this.emailClient = emailClient;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = "http://192.168.1.21:45455/api/store/addStore/" + emailClient + "/" + store.getID() + "/" + store.getName() + "/" + store.getAddress() + "/" + store.getDescription() + "/" + store.getPhoneNumber() + "/" + store.getLatitude() + "/" + store.getLongitude() + "/";
        APIReader http = new APIReader();
        http.getHTTPData(url);
        return null;
    }
}
