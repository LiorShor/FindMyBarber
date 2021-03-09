package com.findmybarber.controller.asynctask;

import android.os.AsyncTask;

import com.findmybarber.controller.asynctask.APIReader;
import com.findmybarber.model.Admin;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GetAdminList extends AsyncTask<Void, Void, List<Admin>> {

    @Override
    protected List<Admin> doInBackground(Void... voids) {
        String url = "http://192.168.1.27:45455/api/user/getUserAdminsList";
        APIReader http = new APIReader();
        String stream = http.getHTTPData(url);
        List<Admin> adminsList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JSONArray object = new JSONArray(stream);
            for (int i = 0; i < object.length(); i++) {
                JSONObject jsonObject = object.getJSONObject(i);
                Admin admin = gson.fromJson(jsonObject.toString(), Admin.class);
                adminsList.add(admin);
            }
            return adminsList;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}