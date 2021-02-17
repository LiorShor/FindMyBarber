package com.findmybarber.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.findmybarber.view.activities.Login;
import org.json.JSONException;
import org.json.JSONObject;

public class Registration {
    public static boolean isEmailExist(String email) {
        return Login.usersList.stream().anyMatch(customer -> customer.getUserEmail().toLowerCase().equals(email.toLowerCase()));
    }
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    public static void volleyPost(Context context, String  firstName, String lastName, String email, String phoneNumber, String password){
        String postUrl = "http://192.168.1.2:45455/api/user/addUser";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("Email", email);
            postData.put("FirstName", firstName);
            postData.put("LastName", lastName);
            postData.put("Password", password);
            postData.put("PhoneNumber", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, response -> {
        }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }
}
