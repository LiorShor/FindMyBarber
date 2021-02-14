package com.findmybarber.model;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddStore extends AsyncTask<Void, Void, Void> {

    private Store store;
    private String emailClient;

    public AddStore(Store store, String emailClient) {
        this.store = store;
        this.emailClient = emailClient;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = "http://192.168.100.1:45455/api/store/addStore/" + emailClient + "/" + store.getID() + "/" + store.getName() + "/" + store.getAddress() + "/" + store.getDescription() + "/" + store.getPhoneNumber() + "/" + store.getLatitude() + "/" + store.getLongitude() + "/";
        APIReader http = new APIReader();
        http.getHTTPData(url);
        return null;
    }
}
