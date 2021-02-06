package com.findmybarber.model;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.findmybarber.view.activities.Login;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.findmybarber.view.activities.Login.dbStoresList;

public class GetDBStoresList  extends AsyncTask<Void, Void, List<Store>> {

    @Override
    protected List<Store> doInBackground(Void... voids) {
        String url = "http://192.168.1.27:45455/api/store/getStoresList";
        APIReader http = new APIReader();
        String stream = http.getHTTPData(url);
        List<Store> storesList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JSONArray object = new JSONArray(stream);
            for (int i = 0; i < object.length(); i++) {
                JSONObject jsonObject = object.getJSONObject(i);
                Store store = gson.fromJson(jsonObject.toString(), Store.class);
                storesList.add(store);
            }

            return storesList;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }


/*
    public void getStoresList() {
        String url = "http://192.168.1.27:45455/api/store/getStoresList";
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
    }*/
}
