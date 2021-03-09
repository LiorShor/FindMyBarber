package com.findmybarber.controller.asynctask;

import android.os.AsyncTask;

import com.findmybarber.controller.asynctask.APIReader;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCoordinatesByAddress extends AsyncTask<Void, Void, String> {

    private String address;

    public GetCoordinatesByAddress(String address) {
        this.address = address;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = "http://api.positionstack.com/v1/forward?access_key=004cfd7f907a6e9cd9d65903873db058&query=" + address;
        APIReader http = new APIReader();
        String stream = http.getHTTPData(url);
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(stream);
            JSONArray jsonMainArray = object.getJSONArray("data");
            JSONObject jsonObject = jsonMainArray.getJSONObject(0);
            String latitude = jsonObject.getString("latitude");
            String longitude = jsonObject.getString("longitude");
            return latitude + "," + longitude;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
