package com.findmybarber.model;

import android.annotation.SuppressLint;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Book {
    private String id;
    private String storeID;
    private String emailClient;
    private String emailStore;
    private String date;
    private Time time;

    @SuppressLint("SimpleDateFormat")
    public Book(String id, String storeID, String emailClient, String emailStore, Calendar calendar) {
        this.id = id;
        this.storeID = storeID;
        this.emailClient = emailClient;
        this.emailStore = emailStore;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.date = df.format(calendar.getTime());
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String s = formatter.format(calendar.getTimeInMillis());
        try {
            this.time = new Time(formatter.parse(s).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public String getEmailStore() {
        return emailStore;
    }

    public void setEmailStore(String emailStore) {
        this.emailStore = emailStore;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
