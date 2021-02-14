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

public class GetBookingForCurrentUserList extends AsyncTask<Void, Void, List<Book>> {

    private final Context context;

    public GetBookingForCurrentUserList(final Context context){
        this.context = context;
    }

    @Override
    protected List<Book> doInBackground(Void... voids) {
        SharedPreferences sharedUserPreferences = context.getSharedPreferences("CurrentUserPref",MODE_PRIVATE);
        String email = sharedUserPreferences.getString("KeyUser", null);
        email = email.split(".com", 2)[0];
        String url = "http://192.168.100.1:45455/api/book/getAppointmentsForCurrentUser/" + email ;
        APIReader http = new APIReader();
        String stream = http.getHTTPData(url);
        List<Book> userAppointmentsList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JSONArray object = new JSONArray(stream);
            for (int i = 0; i < object.length(); i++) {
                JSONObject jsonObject = object.getJSONObject(i);
                String id = jsonObject.getString("ID");
                String storeID = jsonObject.getString("StoreID");
                String emailClient = jsonObject.getString("EmailClient");
                String emailStore = jsonObject.getString("EmailStore");
                String date = jsonObject.getString("Date");
                String time = jsonObject.getString("Time");
                String hour = time.split(":",2)[0];
                String minute = time.split(":",3)[1];
                Calendar calendar = Calendar.getInstance();
                int year = Integer.parseInt(date.split("-",3)[0]);
                int month =  Integer.parseInt(date.split("-",3)[1]);
                int dayOfMonth =  Integer.parseInt(date.split("-",3)[2].split("T",2)[0]);
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(year, month-1, dayOfMonth);
                Book book = new Book(id,storeID,emailClient,emailStore,calendar);
                userAppointmentsList.add(book);
            }
            return userAppointmentsList;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}