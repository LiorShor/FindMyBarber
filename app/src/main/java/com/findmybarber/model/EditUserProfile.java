package com.findmybarber.model;

import android.os.AsyncTask;

import java.util.List;

public class EditUserProfile extends AsyncTask<Void, Void, Void> {

    private String userEmail;
    private String firstName;
    private String lastName;

    public EditUserProfile(String userEmail, String firstName, String lastName) {
        this.userEmail = userEmail.isEmpty()? null : userEmail;
        this.firstName = firstName.isEmpty()? null : firstName;
        this.lastName = lastName.isEmpty()? null : lastName;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = "http://192.168.43.202:45455/api/user/EditUserProfile/" + userEmail + "/" + firstName + "/" + lastName;
        APIReader http = new APIReader();
        http.getHTTPData(url);
        return null;
    }
}
