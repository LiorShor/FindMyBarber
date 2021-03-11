package com.findmybarber.controller.asynctask;

import android.os.AsyncTask;

import com.findmybarber.controller.asynctask.APIReader;
import com.findmybarber.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GetBookingList extends AsyncTask<Void, Void, List<Book>> {
    private final String storeID;

    public GetBookingList(String storeID) {
        this.storeID = storeID;
    }

    @Override
    protected List<Book> doInBackground(Void... voids) {
        String url = "http://192.168.1.21:45455/api/book/getBookingList/" + storeID;
        APIReader http = new APIReader();
        String stream = http.getHTTPData(url);
        List<Book> bookingsList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(stream);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
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
                bookingsList.add(book);
            }
            return bookingsList;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}