package com.findmybarber.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.findmybarber.view.activities.Login.dbStoresList;

public class GetStores extends AsyncTask<Void, Void, List<Store>> {
    private final Context context;
    private double selfLatitude;
    private double selfLongitude;
    private String urlString;
    public static List<Store> storesList = new ArrayList<>();

    public GetStores(final Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        urlString = find_Location(context);
        getStoresList();
    }

    @Override
    protected void onPostExecute(List<Store> result) {
        super.onPostExecute(result);
    }

    @Override
    protected List<Store> doInBackground(Void... params) {
        List<Store> storesList = new ArrayList<>();
        String stream;
        APIReader http = new APIReader();
        stream = http.getHTTPData(urlString);
        try {

            JSONObject object = new JSONObject(stream);
            JSONArray jsonMainArray = object.getJSONArray("results");
            for (int i = 0; i < jsonMainArray.length(); i++) {
                JSONObject jsonObject = jsonMainArray.getJSONObject(i);
                String ID = UUID.randomUUID().toString();
                String name = jsonObject.getString("name");
                String address = jsonObject.getString("vicinity");
                double rank;
                try {
                    rank = Double.parseDouble(jsonObject.getString("rating"));
                } catch (JSONException ex) {
                    rank = 0;
                }
                JSONObject geometry = jsonObject.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double storeLatitude = location.getDouble("lat");
                double storeLongitude = location.getDouble("lng");
                double distance = Math.round(distance(storeLatitude, getSelfLatitude(), storeLongitude, getSelfLongitude(), 0, 0) / 1000 * 100.0) / 100.0;
                Store store = new Store(ID, name, address, rank,"", distance + " km", 0, storeLatitude, storeLongitude);
                store.setDistance(distance);
                storesList.add(store);
            }
            return storesList;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public double getSelfLatitude() {
        return selfLatitude;
    }

    public double getSelfLongitude() {
        return selfLongitude;
    }


    public String find_Location(final Context con) {
        Log.d("Find Location", "in find_location");
        String urlString ="";
        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) con.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return urlString;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {

                selfLatitude = location.getLatitude();
                selfLongitude = location.getLongitude();
                urlString = apiRequest(selfLatitude,selfLongitude);
            }
        }
        return urlString;
    }


    public static String apiRequest(double selfLatitude, double selfLongitude)
    {
        StringBuilder sb = new StringBuilder();
        String API_KEY = "key=AIzaSyCxfal1FttVVLWd6TOwgmQbyE4cZLfWoPA";
        String API_LINK = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String radius = "radius=3000&";
        String type = "type=hair_care&";
        String latitude = String.valueOf(selfLatitude);
        String longitude = String.valueOf(selfLongitude);
        sb.append(API_LINK).append(latitude).append(",").append(longitude).append("&").append(radius).append(type).append(API_KEY);
        return sb.toString();
    }

    public double distance(double lat1, double lat2, double lon1,
                           double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    public void getStoresList() {
        String url = "http://192.168.1.2:45455/api/store/getStoresList";
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Store store = gson.fromJson(jsonObject.toString(), Store.class);
                        dbStoresList.add(store);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (Store store : Login.dbStoresList) {
                    if(Math.round(distance(store.getLatitude(),getSelfLatitude(),store.getLongitude(),getSelfLongitude(),0,0)/ 1000 * 100.0) / 100.0 < 3000) {
                        storesList.add(store);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

}