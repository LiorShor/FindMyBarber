package com.findmybarber.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GetNextSlot extends AsyncTask<Void, Void, String> {
    private final Context context;
    private final String storeID;

    public GetNextSlot(Context context, String storeID) {
        this.context = context;
        this.storeID = storeID;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = "http://192.168.43.202:45455/api/book/getFreeUpcomingTimeSlot/" + storeID ;
        APIReader http = new APIReader();
        return http.getHTTPData(url);
    }
}
