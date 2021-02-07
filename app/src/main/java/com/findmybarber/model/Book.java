package com.findmybarber.model;

import android.annotation.SuppressLint;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Book {
    private String ID;
    private String StoreID;
    private String EmailClient;
    private String EmailStore;
    private String Date;
    private Time Time;

    @SuppressLint("SimpleDateFormat")
    public Book(String ID, String storeID, String emailClient, String emailStore, Calendar calendar) {
        this.ID = ID;
        this.StoreID = storeID;
        this.EmailClient = emailClient;
        this.EmailStore = emailStore;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.Date = df.format(calendar.getTime());
        DateFormat formatter = new SimpleDateFormat("HH mm ss");
        String s = formatter.format(calendar.getTimeInMillis());
        try {
            this.Time = new Time(formatter.parse(s).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getID() {
        return ID;
    }

    public String getStoreID() {
        return StoreID;
    }

    public void setStoreID(String storeID) {
        this.StoreID = storeID;
    }

    public String getEmailClient() {
        return EmailClient;
    }

    public void setEmailClient(String emailClient) {
        this.EmailClient = emailClient;
    }

    public String getEmailStore() {
        return EmailStore;
    }

    public void setEmailStore(String emailStore) {
        this.EmailStore = emailStore;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public Time getTime() {
        return Time;
    }

    public void setTime(Time time) {
        this.Time = time;
    }
}
