package com.findmybarber.model;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetStorePhone extends AsyncTask<Void, Void, String> {
    private final String keywords;
    private final Context context;
    String urlString ="";
    public GetStorePhone(String keywords, Context context) {
        this.keywords = keywords;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        GetLocation getLocation = new GetLocation(context);
        String selfLatitude = String.valueOf(getLocation.getSelfLatitude());
        String selfLongitude = String.valueOf(getLocation.getSelfLongitude());
        urlString = apiRequestGooglePlaces(selfLatitude,selfLongitude);
    }

    @Override
    protected String doInBackground(Void... voids) {
        String stream ="";
        String placeID = "";
        APIReader http = new APIReader();
        stream = http.getHTTPData(urlString);
        try{
            JSONObject object = new JSONObject(stream);
            JSONArray jsonMainArray = object.getJSONArray("results");
            JSONObject jsonObject = jsonMainArray.getJSONObject(0);
             placeID =  jsonObject.getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlString = apiRequestGoogleDetails(placeID);
        stream = http.getHTTPData(urlString);
        try {
            JSONObject object = new JSONObject(stream);
            JSONObject jsonObject = object.getJSONObject("result");
            return jsonObject.getString("formatted_phone_number");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String apiRequestGooglePlaces(String selfLatitude, String selfLongitude) {
        String API_KEY = "key=AIzaSyBoNid_8AbvsVzffJxtz9ODguZRx8Hv6A0";
        String API_LINK = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String radius = "radius=47022&";
        String type = "type=hair_care&";
        String keyword = "&keywords=" + keywords;
        return API_LINK + selfLatitude + "," + selfLongitude + "&" + radius + type + API_KEY + keyword;
    }

    public String apiRequestGoogleDetails(String reqPlaceID){
        String API_LINK = "https://maps.googleapis.com/maps/api/place/details/json?";
        String placeID = "place_id="+ reqPlaceID;
        String filterField = "&fields=formatted_phone_number";
        String API_KEY = "&key=AIzaSyBoNid_8AbvsVzffJxtz9ODguZRx8Hv6A0";
        return API_LINK + placeID + filterField + API_KEY;

    }



    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBoNid_8AbvsVzffJxtz9ODguZRx8Hv6A0&sensor=false&location=51.52864165,-0.10179430&radius=47022&keyword=%22london%20eye%22
//    https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJN1t_tDeuEmsRUsoyG83frY4&fields=name,rating,formatted_phone_number&key=AIzaSyBoNid_8AbvsVzffJxtz9ODguZRx8Hv6A0

}
