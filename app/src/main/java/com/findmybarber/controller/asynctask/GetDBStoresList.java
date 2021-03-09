package com.findmybarber.controller.asynctask;

import android.os.AsyncTask;

import com.findmybarber.controller.asynctask.APIReader;
import com.findmybarber.model.Store;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
}
